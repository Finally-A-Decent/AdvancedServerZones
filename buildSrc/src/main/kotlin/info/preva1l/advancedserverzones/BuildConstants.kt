package info.preva1l.advancedserverzones

import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.konan.file.File

/**
 * Created on 1/07/2025
 *
 * @author Preva1l
 */
object BuildConstants {
    private val ROOT_DIR: File by lazy {
        var dir = File(System.getProperty("user.dir"))
        while (!File(dir, ".git").exists && dir.parentFile.exists) {
            dir = dir.parentFile
        }
        dir
    }

    val DEV_MODE: Boolean by lazy {
        val grgit = Grgit.open { dir = ROOT_DIR }
        val currentBranch: String = grgit.branch.current().name
        val devMode = currentBranch != "master" && currentBranch != "HEAD"
        if (devMode) println("Starting in development mode")
        return@lazy devMode
    }
}
