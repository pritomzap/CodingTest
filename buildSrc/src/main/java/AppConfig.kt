import java.io.File

object AppConfig {
    const val compileSkdVersion = 31
    const val buildToolsVersion = "30.0.3"
    const val minSdkVersion = 21
    const val targetSdkVersion = 31
    const val renderscriptTargetApi = 31
    const val renderscriptSupportModeEnabled = true
    const val multiDexEnabled = true
    const val vectorDrawablesUseSupportLibrary = true
    const val testInstrumentationRunner = "com.meldcx.codingtest.HiltTestRunner"

    const val applicationId = "com.meldcx.codingtest"
    const val versionCode = 1
    const val versionName = "1.0.0"
    const val appVarientName = "MeldCxCodingText"

    fun getRoomAnnotationProcessorArgMap(projectDir:File) = mapOf(
        "room.schemaLocation" to "$projectDir/schemas",
        "room.incremental" to "true",
        "room.expandProjection" to "true"
    )

}