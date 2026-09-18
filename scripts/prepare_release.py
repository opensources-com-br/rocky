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
