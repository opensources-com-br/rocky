import AppKit
import CoreText

// Match apps/web/app/icon.svg: orange rounded square and dark Arial Bold R.
let output = CommandLine.arguments[1]
let size = 1024
let scale = CGFloat(size) / 32
let bitmap = NSBitmapImageRep(bitmapDataPlanes: nil, pixelsWide: size, pixelsHigh: size,
    bitsPerSample: 8, samplesPerPixel: 4, hasAlpha: true, isPlanar: false,
    colorSpaceName: .deviceRGB, bytesPerRow: 0, bitsPerPixel: 0)!
NSGraphicsContext.saveGraphicsState()
NSGraphicsContext.current = NSGraphicsContext(bitmapImageRep: bitmap)
NSColor(srgbRed: 212/255, green: 112/255, blue: 60/255, alpha: 1).setFill()
NSBezierPath(roundedRect: NSRect(x: 0, y: 0, width: size, height: size),
    xRadius: 8 * scale, yRadius: 8 * scale).fill()
let font = NSFont(name: "Arial-BoldMT", size: 22 * scale)!
let text = NSAttributedString(string: "R", attributes: [
    .font: font,
    .foregroundColor: NSColor(srgbRed: 20/255, green: 10/255, blue: 5/255, alpha: 1),
])
let line = CTLineCreateWithAttributedString(text)
let width = CTLineGetTypographicBounds(line, nil, nil, nil)
let context = NSGraphicsContext.current!.cgContext
context.textPosition = CGPoint(x: (CGFloat(size) - width) / 2, y: 9 * scale)
CTLineDraw(line, context)
NSGraphicsContext.restoreGraphicsState()
try bitmap.representation(using: .png, properties: [:])!.write(to: URL(fileURLWithPath: output))
