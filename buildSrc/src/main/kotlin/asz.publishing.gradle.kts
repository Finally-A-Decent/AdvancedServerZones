import info.preva1l.advancedserverzones.BuildConstants
import info.preva1l.trashcan.finallyADecent

plugins {
    `maven-publish`
    id("info.preva1l.trashcan")
}

publishing {
   publications {
        repositories.finallyADecent(dev = BuildConstants.DEV_MODE, authenticated = true)
    }
}