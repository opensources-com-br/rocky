"""Verify that a packaged Rocky candidate is self-contained and traceable."""
import hashlib
import json
import pathlib


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
