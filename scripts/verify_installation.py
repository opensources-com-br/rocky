"""Verify that a packaged Rocky candidate is self-contained and traceable."""
import hashlib
import json
import os
import pathlib
import platform
import subprocess


def sha256(path):
    digest = hashlib.sha256()
    with path.open('rb') as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b''):
            digest.update(block)
    return digest.hexdigest()


def verify_asset(root, asset):
    matches = list(root.glob(f"*/{asset['file']}"))
    if len(matches) != 1:
        raise ValueError(f"Expected one installer named {asset['file']}")
    if sha256(matches[0]) != asset['sha256']:
        raise ValueError(f"Checksum mismatch for {asset['file']}")


def verify_metadata(metadata, properties, commit, system, architecture):
    expected = {
        'version': properties['rockyVersion'],
        'native_version': properties['rockyPackageVersion'],
        'commit': commit,
        'os': system,
        'architecture': architecture,
    }
    for key, value in expected.items():
        if metadata.get(key) != value:
            raise ValueError(f"Unexpected {key}: {metadata.get(key)}")


def verify_runtime(root, system):
    if len(list(root.glob('app/**/runtime/**/release'))) != 1:
        raise ValueError('The packaged Java runtime is missing')
    pattern = 'app/*.app/Contents/MacOS/Rocky' if system == 'darwin' else 'app/**/Rocky.exe'
    if len(list(root.glob(pattern))) != 1:
        raise ValueError('The Rocky launcher is missing')


def main():
    root = pathlib.Path(__file__).resolve().parents[1]
    binary_root = root / 'apps/desktop/build/compose/binaries/main'
    lines = (root / 'gradle.properties').read_text().splitlines()
    properties = dict(line.split('=', 1) for line in lines if '=' in line)
    system, architecture = platform.system().lower(), platform.machine().lower()
    metadata_files = list(binary_root.glob(f'BUILDINFO-{system}-{architecture}.json'))
    if len(metadata_files) != 1:
        raise ValueError('Expected exactly one BUILDINFO file')
    metadata = json.loads(metadata_files[0].read_text())
    if not metadata.get('assets'):
        raise ValueError('No installers were recorded')
    commit = os.environ.get('GITHUB_SHA') or subprocess.check_output(
        ['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip()
    verify_metadata(metadata, properties, commit, system, architecture)
    for asset in metadata.get('assets', []):
        verify_asset(binary_root, asset)
    verify_runtime(binary_root, system)


if __name__ == '__main__':
    main()
