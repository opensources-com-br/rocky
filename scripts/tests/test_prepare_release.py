import json
import pathlib
import tempfile
import unittest

from scripts.prepare_release import TARGETS, checksum, native_version, prepare, verify_upgrade


class PrepareReleaseTest(unittest.TestCase):
    def test_requires_native_upgrade_even_for_alpha(self):
        with self.assertRaisesRegex(ValueError, 'increase the native version'):
            verify_upgrade('v1.2.3-alpha.2', [{'draft': False, 'tag_name': 'v1.2.3-alpha.1'}])
        verify_upgrade('v1.2.4-alpha.1', [[{'draft': False, 'tag_name': 'v1.2.3-alpha.2'}]])
        verify_upgrade('v1.2.4-alpha.1', [{'draft': True, 'tag_name': 'v2.0.0'}])

    def test_rejects_native_overflow_and_invalid_tags(self):
        for version in ('256.0.1', '1.256.1', '1.0.65536', '1.0.0-beta.1', '../v1.0.0'):
            with self.subTest(version=version), self.assertRaises(ValueError):
                native_version(version)

    def candidate(self, root):
        for (system, arch), extensions in TARGETS.items():
            directory = root / (system + '-' + arch)
            directory.mkdir(parents=True)
            assets = []
            for extension in extensions:
                asset = directory / f'Rocky-1.2.4-alpha.1-{system}-{arch}.{extension}'
                asset.write_bytes(b'package fixture')
                assets.append({'file': asset.name, 'sha256': checksum(asset)})
            metadata = {'version': '1.2.4-alpha.1', 'native_version': '1.2.4', 'commit': 'revision',
                        'os': system, 'architecture': arch, 'assets': assets}
            (directory / f'BUILDINFO-{system}-{arch}.json').write_text(json.dumps(metadata))

    def test_flattens_complete_release_with_matching_checksums(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = pathlib.Path(temporary)
            self.candidate(root / 'source')
            prepare(root / 'source', root / 'ready', 'v1.2.4-alpha.1', 'revision')
            sums = (root / 'ready/SHA256SUMS.txt').read_text().splitlines()
            self.assertEqual(7, len(sums))
