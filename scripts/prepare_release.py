"""Validate all native packages before exposing a release to the updater."""
import argparse
import hashlib
import json
import pathlib
import re
import shutil


TARGETS = {('darwin', 'arm64'): ('dmg',), ('darwin', 'x86_64'): ('dmg',),
           ('windows', 'amd64'): ('msi', 'exe')}


def native_version(version):
    match = re.fullmatch(r'v?(\d+)\.(\d+)\.(\d+)(?:-alpha\.(\d+))?', version)
    if not match:
        raise ValueError(f'Unsupported release version: {version}')
    native = tuple(int(part) for part in match.groups()[:3])
    if not (1 <= native[0] <= 255 and native[1] <= 255 and native[2] <= 65535):
        raise ValueError('Release version exceeds native installer limits')
    return native


def verify_upgrade(version, releases):
    candidate = native_version(version)
    for record in releases:
        if isinstance(record, list):
            verify_upgrade(version, record)
        elif not record.get('draft', True):
            try:
                previous = native_version(record.get('tag_name', ''))
            except ValueError:
                continue
            if previous >= candidate:
                raise ValueError('Every release must increase the native version, including alphas')


def prepare(source, destination, version, commit):
    native = '.'.join(map(str, native_version(version)))
    version = version.removeprefix('v')
    files = list(source.rglob('*'))
    recorded = {}
    for (system, arch), extensions in TARGETS.items():
        name = f'BUILDINFO-{system}-{arch}.json'
        matches = [path for path in files if path.name == name]
        if len(matches) != 1:
            raise ValueError(f'Expected exactly one {name}')
        metadata = json.loads(matches[0].read_text())
        identity = {'version': version, 'native_version': native, 'commit': commit,
                    'os': system, 'architecture': arch}
        if any(metadata.get(key) != value for key, value in identity.items()):
            raise ValueError(f'Incorrect build identity: {name}')
        expected = {f'Rocky-{version}-{system}-{arch}.{ext}' for ext in extensions}
        assets = metadata.get('assets', [])
        if len(assets) != len(expected) or {asset['file'] for asset in assets} != expected:
            raise ValueError(f'Incomplete packages: {name}')
        recorded[name] = matches[0]
        for asset in assets:
            matches = [path for path in files if path.name == asset['file']]
            if len(matches) != 1 or checksum(matches[0]) != asset['sha256']:
                raise ValueError(f'Invalid package: {asset["file"]}')
            recorded[asset['file']] = matches[0]
    destination.mkdir(parents=True, exist_ok=False)
    for name, path in recorded.items():
        shutil.copyfile(path, destination / name)
    sums = [f'{checksum(destination / name)}  {name}\n' for name in sorted(recorded)]
    (destination / 'SHA256SUMS.txt').write_text(''.join(sums), encoding='utf-8')


def checksum(path):
    digest = hashlib.sha256()
    with path.open('rb') as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b''):
            digest.update(block)
    return digest.hexdigest()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--version', required=True)
