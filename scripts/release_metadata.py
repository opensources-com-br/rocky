"""Name release assets unambiguously and record their source revision and architecture."""
import argparse
import hashlib
import json
import os
import pathlib
import platform
import subprocess


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--check-tag', action='store_true')
    args = parser.parse_args()
    root = pathlib.Path(__file__).resolve().parents[1]
    props = dict(line.split('=', 1) for line in (root / 'gradle.properties').read_text().splitlines() if '=' in line)
    version = props['rockyVersion']
    if args.check_tag:
        if os.environ.get('GITHUB_REF_NAME') != 'v' + version:
            raise SystemExit('The release tag must match rockyVersion in gradle.properties.')
        return
    binary_root = root / 'apps/desktop/build/compose/binaries/main'
    system = platform.system().lower()
    arch = platform.machine().lower()
    files = []
    for extension in ('dmg', 'msi', 'exe'):
        for source in (binary_root / extension).glob('*.' + extension):
            target = source.with_name(f'Rocky-{version}-{system}-{arch}.{extension}')
            if target != source:
                source.rename(target)
            files.append({'file': target.name, 'sha256': hashlib.sha256(target.read_bytes()).hexdigest()})
    if not files:
        raise SystemExit('No installers found; build native packages first.')
    commit = os.environ.get('GITHUB_SHA') or subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip()
    metadata = {'version': version, 'native_version': props['rockyPackageVersion'], 'commit': commit,
                'os': system, 'architecture': arch, 'assets': files}
    (binary_root / f'BUILDINFO-{system}-{arch}.json').write_text(json.dumps(metadata, indent=2) + '\n')


if __name__ == '__main__':
    main()
