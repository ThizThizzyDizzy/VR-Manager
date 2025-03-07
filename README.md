# VR Manager

This is a Java program made for starting up, monitoring, and shutting down complicated VR setups.

## Installation

Download the latest version from [Releases](https://github.com/ThizThizzyDizzy/VR-Manager/releases)

There are two ways to install, either via the `.exe` installer or by extracting the `.zip` and running with java.

### Installer (Recommended)

Download and run the installer. It will create a desktop shortcut that can be used to run the program.

There are three installers provided. **These all install the same program**, but configure it to open slightly differently.

- `VR Manager-GUI-X.X.X.exe` - VR Manager will open with **GUI Only**. This is the simplest way to use VR Manager.
- `VR Manager-X.X.X.exe` - VR Manager will open with both **GUI and Console**.
- `VR Manager-Console-X.X.X.exe` - VR Manager will open with **Console Only**. This is **Not recommended** for most users.

(Any version can be configured to run like any other; these are just separated for convenience)

*VR Manager will be installed in your Local AppData Folder.*

### Manual Installation

You will need Java 17 or higher installed.

Download and extract the VR Manager archive (`VR-Manager-X.X.X.zip`) into a new folder. Run the VR Manager JAR file.

## Launch Arguments

There are two launch arguments:

- `nogui` - This hides the GUI. This is automatically added when running the (GUI) version.
- `init` - This will initialize & start all configured modules on startup.