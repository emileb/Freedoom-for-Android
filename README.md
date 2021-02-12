# Note
This repository is a fork of Mkrupzcak3's Freedoom, with the goal
of making this app fully libre and updating to newer versions
of GZDoom and TouchControls.  

Currently, there's no active development of this fork or
original Mkrupzcak3's Freedoom.  

Please refer to [original repository](https://github.com/mkrupczak3/Freedoom-for-Android) and [original README](https://github.com/mkrupczak3/Freedoom-for-Android/blob/master/README.md)
for more information.  

## Build instructions
On windows, build should be performed in a cygwin environment.  

NDK r21d is required for compilation.  

- Initialize git submodules with `git submodule update --init`
- Add your NDK directory to your `$PATH`
- Change your current directory to doom/src/main/jni
- Run `ndk-build`
- Change your working directory to `doom`
- run ../gradlew assembleRelease

Example:
```
export PATH=$PATH:path/to/your/ndk/directory
git submodule update --init
pushd doom/src/main/jni
ndk-build
popd
cd doom
../gradlew assembleRelease
```
