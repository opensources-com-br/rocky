"""Exercise the real updater shell using only temporary app and disk-image fixtures."""
import hashlib
import os
import pathlib
import plistlib
import shlex
import subprocess
import sys
import tempfile
import time
import unittest


ROOT = pathlib.Path(__file__).resolve().parents[2]
HELPER = ROOT / 'shared/data/src/desktopMain/resources/updates/install-macos.sh'
STUB = '''#!/usr/bin/env python3
import os, pathlib, plistlib, shutil, sys
root = pathlib.Path(os.environ['ROCKY_TEST_ROOT']).resolve()
command, args = pathlib.Path(sys.argv[0]).name, sys.argv[1:]
def safe(value):
    path = pathlib.Path(value).resolve()
    assert path == root or root in path.parents, 'Path outside updater fixture'
    return path
if command == 'plutil':
    print(plistlib.loads(safe(args[-1]).read_bytes())[args[1]])
elif command == 'hdiutil':
    if args[0] == 'attach':
        safe(args[1])
        mount = safe(args[args.index('-mountpoint') + 1])
        shutil.copytree(root / 'payload/Rocky.app', mount / 'Rocky.app')
    else:
        for child in safe(args[1]).iterdir():
            shutil.rmtree(child) if child.is_dir() else child.unlink()
elif command == 'codesign':
    app = safe(args[-1])
    if '--verify' in args:
        sys.exit(1 if (app / 'reject-signature').exists() else 0)
    print('TeamIdentifier=' + (app / 'team').read_text(), file=sys.stderr)
elif command == 'ditto':
    shutil.copytree(safe(args[0]), safe(args[1]))
elif command == 'open':
    app = safe(args[0])
    version = (app / 'Contents/MacOS/Rocky').read_text()
    with (root / 'opened').open('a') as log:
        log.write(version + '\\n')
    sys.exit(1 if version == 'new' and os.environ.get('ROCKY_TEST_FAIL_OPEN') else 0)
elif command == 'ps':
    print(os.environ['ROCKY_TEST_PARENT_PID'], root / 'Applications/Rocky.app/Contents/MacOS/Rocky')
    print('999998', root / 'other/Rocky.app/Contents/MacOS/Rocky')
    if (root / 'extra-instance').exists():
        print('999999', root / 'Applications/Rocky.app/Contents/MacOS/Rocky')
else:
    raise AssertionError(command)
'''


@unittest.skipIf(os.name == 'nt', 'The macOS updater uses a POSIX shell.')
class MacosUpdaterTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory(prefix='rocky updater ')
        self.addCleanup(temporary.cleanup)
        self.root = pathlib.Path(temporary.name).resolve()
        self.target = self.root / 'Applications/Rocky.app'
        self.payload = self.root / 'payload/Rocky.app'
        self.job = self.root / 'job'; self.job.mkdir()
        self.package = self.root / 'Rocky.dmg'
        self.package.write_bytes(b'only a disk-image fixture, never mounted')
        self.digest = hashlib.sha256(self.package.read_bytes()).hexdigest()
        self.create_app(self.target, 'old', '1.0.0')
        self.create_app(self.payload, 'new', '2.0.0')
        self.environment = dict(os.environ, ROCKY_TEST_ROOT=str(self.root))
        self.helper = self.root / 'install-macos.sh'
        source = HELPER.read_text()
        allowed = '/Applications/Rocky.app|"$HOME/Applications/Rocky.app"'
        self.assertIn(allowed, source)
        source = source.replace(allowed, shlex.quote(str(self.target)))
        for command in ('plutil', 'hdiutil', 'codesign', 'ditto', 'open', 'ps'):
            stub = self.root / command
            stub.write_text(STUB); stub.chmod(0o700)
            absolute = ('/bin/' if command == 'ps' else '/usr/bin/') + command
            self.assertIn(absolute, source)
            source = source.replace(absolute, shlex.quote(str(stub)))
        self.assertNotIn('$HOME', source)
        self.helper.write_text(source)
        self.parent = subprocess.Popen([sys.executable, '-c', 'import time; time.sleep(60)'])
        self.addCleanup(self.stop_process, self.parent)
        self.environment['ROCKY_TEST_PARENT_PID'] = str(self.parent.pid)

    @staticmethod
    def stop_process(process):
        if process.poll() is None:
            process.terminate()
        try:
            process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            process.kill(); process.wait(timeout=5)

    @staticmethod
    def create_app(app, marker, version):
        launcher = app / 'Contents/MacOS/Rocky'
        launcher.parent.mkdir(parents=True)
        launcher.write_text(marker); launcher.chmod(0o700)
        (app / 'team').write_text('ROCKYTEAM')
        (app / 'Contents/Info.plist').write_bytes(plistlib.dumps({
            'CFBundleIdentifier': 'dev.rocky.app', 'CFBundleShortVersionString': version,
        }))
        config = app / 'Contents/app/Rocky.cfg'
        config.parent.mkdir()
        config.write_text('java-options=-Drocky.version=2.0.0-alpha.1\n')

    def start(self, digest=None):
        process = subprocess.Popen([
            '/bin/bash', str(self.helper), str(self.package), str(self.target),
            str(self.parent.pid), str(self.job), digest or self.digest, '2.0.0', '2.0.0-alpha.1',
        ], env=self.environment, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        self.addCleanup(self.stop_process, process)
        self.addCleanup(process.stdout.close)
        self.addCleanup(process.stderr.close)
        return process

    def ready(self, process):
        deadline = time.monotonic() + 10
        while not (self.job / 'ready').exists() and process.poll() is None:
            if time.monotonic() > deadline:
                self.fail('The updater did not finish preparation in time.')
            time.sleep(0.02)
        self.assertTrue((self.job / 'ready').exists())
        self.assertEqual('ready', (self.job / 'status').read_text().strip())

    def finish(self, process, status):
        output, error = process.communicate(timeout=10)
        self.assertEqual(status, (self.job / 'status').read_text().strip(), output + error)
        self.assertEqual(0 if status == 'installed' else 1, process.returncode, output + error)
        self.assertFalse(list(self.target.parent.glob('.Rocky-update.*')))

    def marker(self):
        return (self.target / 'Contents/MacOS/Rocky').read_text()

    def test_waits_for_parent_then_replaces_and_relaunches(self):
        process = self.start(); self.ready(process)
