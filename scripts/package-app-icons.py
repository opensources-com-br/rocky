"""Generate desktop icons on macOS. Run from any directory with Python 3."""
from pathlib import Path
import struct
import subprocess
import tempfile

root = Path(__file__).resolve().parent.parent
source = root / "apps/desktop/src/jvmMain/resources/rocky.png"
icons = root / "apps/desktop/icons"
subprocess.run(["swift", str(root / "scripts/generate-app-icon.swift"), str(source)], check=True)

with tempfile.TemporaryDirectory(prefix="rocky-icons-") as temporary:
    folder = Path(temporary)
    iconset = folder / "rocky.iconset"
    iconset.mkdir()

    def resize(size, destination):
        subprocess.run(["sips", "-z", str(size), str(size), str(source),
                        "--out", str(destination)], check=True, stdout=subprocess.DEVNULL)

    for size in (16, 32, 128, 256, 512):
        resize(size, iconset / f"icon_{size}x{size}.png")
        resize(size * 2, iconset / f"icon_{size}x{size}@2x.png")
    subprocess.run(["iconutil", "-c", "icns", str(iconset),
                    "-o", str(icons / "rocky.icns")], check=True)

    sizes = (16, 24, 32, 48, 64, 128, 256)
    images = []
    for size in sizes:
        image = folder / f"windows-{size}.png"
        resize(size, image)
        images.append(image.read_bytes())
    # ICO stores a directory followed by PNG images, including a 256px entry.
    offset = 6 + 16 * len(images)
    directory = bytearray(struct.pack("<HHH", 0, 1, len(images)))
    for size, image in zip(sizes, images):
        directory.extend(struct.pack("<BBBBHHII", size % 256, size % 256,
                                     0, 0, 1, 32, len(image), offset))
        offset += len(image)
    (icons / "rocky.ico").write_bytes(directory + b"".join(images))
