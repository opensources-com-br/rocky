import pathlib
import tempfile
import unittest

from scripts.verify_installation import sha256, verify_asset, verify_metadata, verify_runtime
from scripts.release_metadata import verify_tag, verify_versions


class InstallationVerificationTest(unittest.TestCase):
    def test_rejects_native_version_from_another_release(self):
        with self.assertRaisesRegex(ValueError, 'rockyPackageVersion'):
            verify_versions('1.2.3-alpha.1', '1.2.2')

    def test_accepts_native_version_matching_the_candidate_base(self):
        verify_versions('1.2.3-alpha.1', '1.2.3')

    def test_rejects_tag_from_another_candidate(self):
        with self.assertRaisesRegex(ValueError, 'release tag'):
            verify_tag('v1.2.3-alpha.2', '1.2.3-alpha.1')

    def test_accepts_tag_matching_the_candidate(self):
        verify_tag('v1.2.3-alpha.1', '1.2.3-alpha.1')

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

    def test_accepts_bundled_macos_runtime(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = pathlib.Path(temporary)
            release = root / 'app/Rocky.app/Contents/runtime/Contents/Home/release'
            launcher = root / 'app/Rocky.app/Contents/MacOS/Rocky'
            release.parent.mkdir(parents=True)
            launcher.parent.mkdir(parents=True)
            release.touch(); launcher.touch()

            verify_runtime(root, 'darwin')

    def test_accepts_bundled_windows_runtime(self):
        with tempfile.TemporaryDirectory() as temporary:
            root = pathlib.Path(temporary)
            release = root / 'app/Rocky/runtime/release'
            launcher = root / 'app/Rocky/Rocky.exe'
            release.parent.mkdir(parents=True)
            launcher.parent.mkdir(parents=True, exist_ok=True)
            release.touch(); launcher.touch()

            verify_runtime(root, 'windows')

    def test_rejects_package_without_java_runtime(self):
        with tempfile.TemporaryDirectory() as temporary:
            with self.assertRaisesRegex(ValueError, 'Java runtime'):
                verify_runtime(pathlib.Path(temporary), 'darwin')


if __name__ == '__main__':
    unittest.main()
