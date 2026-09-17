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


if __name__ == '__main__':
    unittest.main()
