# Nasal Demons

A SuperCollider UGen to play arbitrary memory regions

Author: Gianluca Elia

This has been developed and tested only on Linux so far

### Requirements

- CMake >= 3.5
- SuperCollider source code

### Building

Clone the project:

    git clone https://elgiano/nasaldemons
    cd NasalDemons
    mkdir build
    cd build

Then, use CMake to configure it:

    cmake .. -DCMAKE_BUILD_TYPE=Release

You may want to manually specify the install location in the first step to point it at your
SuperCollider extensions directory: add the option `-DCMAKE_INSTALL_PREFIX=/path/to/extensions`.
(you can get your extensions path by running `Platform.userExtensionDir` in SuperCollider)

Build it and install it:
    cmake --build . --config Release
    cmake --build . --config Release --target install


It's expected that the SuperCollider repo is cloned at `../supercollider` relative to this repo. If
it's not: add the option `-DSC_PATH=/path/to/sc/source`.

### Developing

Use the command in `regenerate` to update CMakeLists.txt when you add or remove files from the
project. You don't need to run it if you only change the contents of existing files. You may need to
edit the command if you add, remove, or rename plugins, to match the new plugin paths. Run the
script with `--help` to see all available options.
