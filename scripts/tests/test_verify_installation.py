import pathlib
import tempfile
import unittest

from scripts.verify_installation import sha256, verify_asset, verify_metadata, verify_runtime


class InstallationVerificationTest(unittest.TestCase):
    def test_accepts_installer_in_path_with_spaces_and_accents(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = pathlib.Path(temporary) / 'instalação Rocky'
            installer = root / 'dmg' / 'Rocky.dmg'
            installer.parent.mkdir(parents=True)
            installer.write_bytes(b'candidate')

            verify_asset(root, {'file': 'Rocky.dmg', 'sha256': sha256(installer)})

    def test_rejects_modified_installer(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = pathlib.Path(temporary)
            installer = root / 'msi' / 'Rocky.msi'
            installer.parent.mkdir()
            installer.write_bytes(b'modified')

            with self.assertRaisesRegex(ValueError, 'Checksum mismatch'):
                verify_asset(root, {'file': 'Rocky.msi', 'sha256': '0' * 64})

    def test_accepts_matching_package_identity(self):
        properties = {'rockyVersion': '1.2.3-alpha.1', 'rockyPackageVersion': '1.2.3'}
        metadata = {'version': '1.2.3-alpha.1', 'native_version': '1.2.3',
                    'commit': 'abc123', 'os': 'darwin', 'architecture': 'arm64'}

        verify_metadata(metadata, properties, 'abc123', 'darwin', 'arm64')

    def test_rejects_wrong_package_architecture(self):
        properties = {'rockyVersion': '1.2.3-alpha.1', 'rockyPackageVersion': '1.2.3'}
        metadata = {'version': '1.2.3-alpha.1', 'native_version': '1.2.3',
                    'commit': 'abc123', 'os': 'windows', 'architecture': 'arm64'}

        with self.assertRaisesRegex(ValueError, 'architecture'):
            verify_metadata(metadata, properties, 'abc123', 'windows', 'amd64')

    def test_rejects_wrong_package_version(self):
        properties = {'rockyVersion': '1.2.3-alpha.1', 'rockyPackageVersion': '1.2.3'}
        metadata = {'version': '1.2.2', 'native_version': '1.2.3',
                    'commit': 'abc123', 'os': 'darwin', 'architecture': 'arm64'}

        with self.assertRaisesRegex(ValueError, 'version'):
            verify_metadata(metadata, properties, 'abc123', 'darwin', 'arm64')

    def test_rejects_wrong_package_commit(self):
        properties = {'rockyVersion': '1.2.3-alpha.1', 'rockyPackageVersion': '1.2.3'}
        metadata = {'version': '1.2.3-alpha.1', 'native_version': '1.2.3',
                    'commit': 'old123', 'os': 'darwin', 'architecture': 'arm64'}

        with self.assertRaisesRegex(ValueError, 'commit'):
            verify_metadata(metadata, properties, 'new456', 'darwin', 'arm64')


if __name__ == '__main__':
    unittest.main()
