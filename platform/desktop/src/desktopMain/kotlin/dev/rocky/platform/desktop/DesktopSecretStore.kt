package dev.rocky.platform.desktop

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.platform.win32.Crypt32Util
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

internal interface SecretStore {
    fun read(): String?
    fun write(value: String)
    fun delete()
}

internal fun desktopSecretStore(namespace: String = "ai"): SecretStore = when {
    System.getProperty("os.name").lowercase().contains("mac") -> MacSecretStore(
        serviceName = "dev.rocky.$namespace",
        accountName = if (namespace == "ai") "active-provider" else "active-credential",
    )
    System.getProperty("os.name").lowercase().contains("win") -> WindowsSecretStore(
        RockyDesktopPaths.notesDatabase.resolveSibling("$namespace-credential.dpapi"),
    )
    else -> error("Secure credential storage is unavailable on this system")
}

// User-scoped DPAPI: no machine-wide encryption and no plaintext temporary files.
internal class WindowsSecretStore(private val path: Path) : SecretStore {
    override fun read(): String? {
        if (!Files.exists(path)) return null
        val bytes = Crypt32Util.cryptUnprotectData(Files.readAllBytes(path))
        return try { bytes.toString(Charsets.UTF_8) } finally { bytes.fill(0) }
    }
    override fun write(value: String) {
        Files.createDirectories(path.parent)
        val bytes = value.toByteArray(Charsets.UTF_8)
        val encrypted = try { Crypt32Util.cryptProtectData(bytes) } finally { bytes.fill(0) }
        val temporary = Files.createTempFile(path.parent, "credential-", ".tmp")
        try {
            Files.write(temporary, encrypted)
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING)
        } finally { Files.deleteIfExists(temporary) }
    }
    override fun delete() { Files.deleteIfExists(path) }
}

// Native Keychain calls keep the password out of command-line arguments and process output.
internal class MacSecretStore(serviceName: String, accountName: String) : SecretStore {
    private val security by lazy { Native.load("Security", Security::class.java) }
    private val core by lazy { Native.load("CoreFoundation", Core::class.java) }
    private val service = serviceName.toByteArray()
    private val account = accountName.toByteArray()

    override fun read(): String? {
        val size = IntByReference()
        val data = PointerByReference()
        val result = security.SecKeychainFindGenericPassword(null, service.size, service, account.size, account, size, data, null)
        if (result == NOT_FOUND) return null
        checked(result)
        return try {
            val bytes = data.value.getByteArray(0, size.value)
            try { bytes.toString(Charsets.UTF_8) } finally { bytes.fill(0) }
        } finally { security.SecKeychainItemFreeContent(null, data.value) }
    }
    override fun write(value: String) {
        val item = PointerByReference()
        val found = security.SecKeychainFindGenericPassword(null, service.size, service, account.size, account, null, null, item)
        val bytes = value.toByteArray(Charsets.UTF_8)
        try {
            if (found == NOT_FOUND) {
                checked(security.SecKeychainAddGenericPassword(null, service.size, service, account.size, account, bytes.size, bytes, null))
            } else {
                checked(found)
                try { checked(security.SecKeychainItemModifyAttributesAndData(item.value, null, bytes.size, bytes)) }
                finally { core.CFRelease(item.value) }
            }
        } finally { bytes.fill(0) }
    }
    override fun delete() {
        val item = PointerByReference()
        val found = security.SecKeychainFindGenericPassword(null, service.size, service, account.size, account, null, null, item)
        if (found == NOT_FOUND) return
        checked(found)
        try { checked(security.SecKeychainItemDelete(item.value)) } finally { core.CFRelease(item.value) }
    }
    private fun checked(status: Int) { check(status == 0) { "Não foi possível acessar o cofre do sistema (código $status)." } }
    private interface Core : Library { fun CFRelease(value: Pointer) }
    private interface Security : Library {
        fun SecKeychainFindGenericPassword(keychain: Pointer?, serviceLength: Int, service: ByteArray, accountLength: Int, account: ByteArray, length: IntByReference?, data: PointerByReference?, item: PointerByReference?): Int
        fun SecKeychainAddGenericPassword(keychain: Pointer?, serviceLength: Int, service: ByteArray, accountLength: Int, account: ByteArray, length: Int, data: ByteArray, item: PointerByReference?): Int
        fun SecKeychainItemModifyAttributesAndData(item: Pointer, attributes: Pointer?, length: Int, data: ByteArray): Int
        fun SecKeychainItemDelete(item: Pointer): Int
        fun SecKeychainItemFreeContent(attributes: Pointer?, data: Pointer): Int
    }
    private companion object { const val NOT_FOUND = -25300 }
}
