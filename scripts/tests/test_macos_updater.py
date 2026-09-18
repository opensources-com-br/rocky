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
