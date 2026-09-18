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
