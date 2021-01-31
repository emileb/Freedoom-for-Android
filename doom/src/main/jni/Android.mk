
TOP_DIR := $(call my-dir)

LOCAL_PATH := $(call my-dir)

include $(TOP_DIR)/jwzgles/Android.mk

include $(TOP_DIR)/AudioLibs_OpenTouch/libsndfile-android/jni/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/libmpg123/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/fluidsynth-lite/src/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/openal/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/FMOD_Studio/Android.mk
include $(TOP_DIR)/AudioLibs_OpenTouch/android_external_flac/Android.mk

include $(TOP_DIR)/jpeg8d/Android.mk
include $(TOP_DIR)/gzdoom_android/mobile/Android.mk

include $(TOP_DIR)/MobileTouchControls/Android.mk

include $(TOP_DIR)/SDL/Android.mk
