rootProject.name = "KamiCommonNMS"

include(":api")

include("versions:v1_8_R1")
include("versions:v1_8_R2")
include("versions:v1_8_R3")
include("versions:v1_9_R1")
include("versions:v1_9_R2")
include("versions:v1_10_R1")
include("versions:v1_11_R1")
include("versions:v1_12_R1")
include("versions:v1_13_R1")
include("versions:v1_13_R2")
include("versions:v1_14_R1")
include("versions:v1_15_R1")
include("versions:v1_16_R1")
include("versions:v1_16_R2")
include("versions:v1_16_R3")
include("versions:v1_17_R1")
include("versions:v1_18_R1")
include("versions:v1_18_R2")
include("versions:v1_19_R1")
include("versions:v1_19_R2")
include("versions:v1_19_R3")
include("versions:v1_20_R1")
include("versions:v1_20_R2")
include("versions:v1_20_R3")
include("versions:v1_20_CB")
include("versions:v1_21_4")
include("versions:v_latest")
include("versions:worlds6")
include("versions:worlds7")

include(":core")
include(":versions")

pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.10"
    }
}