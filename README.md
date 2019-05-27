# Yet Another Build Visualizer Plug-in
A Jenkins plugin that allows visualization of the build flow of upstream and
downstream builds in Jenkins.

## Screenshot

![Screenshot](https://raw.githubusercontent.com/jenkinsci/yet-another-build-visualizer-plugin/master/static/yabv.png)

*With all info switches enabled:*

![Screenshot](https://raw.githubusercontent.com/jenkinsci/yet-another-build-visualizer-plugin/master/static/yabv_toggles.png)

## Main Features

* It displays not only downstream builds, but the *full* build flow. In the
  screenshot above, the build currently visited is highlighted using a thick
  dashed border.

* Since the full chain is always shown, the plugin provides easy and fast
  navigation between all executed builds in the pipeline.

* Sports nice and modern interface.

* It is compatible with all build types in Jenkins and all known mechanisms of
  triggering downstream builds.

* Switches to enable displaying of build history and build time information.

* Provides visualization without adding actions or tagging builds in Jenkins
  with additional meta-data, hence it is totally non-destructive and safe for
  removal without risking serialization issues.

## Usage

* No configuration is needed, just install the plugin and you are ready to go.

* The "Build Flow"-graph (as seen in the screenshot above) can be found on the
  Project page (shows the graph for the latest build) or on the Build status
  page. The graph is only shown for builds that has at least one upstream or
  downstream job.

* Click the build links to go to the status page of that build. Note that all
  links are decorated with drop down menus, allowing quick access to common
  pages (console, parameters info, etc).

* This plugin currently only supports Classic UI. Blue Ocean support may follow
  in future versions.

* The intervals at which the Build Flow graph refreshes (default: *10000ms*) can
  be overridden using the Java System Property `yabv.buildFlowRefreshInterval`.
  The expected unit is ms. E.g. `-D yabv.buildFlowRefreshInterval=5000`. Notice
  that the unit suffix is not included. Set `yabv.buildFlowRefreshInterval=0` to
  completely disable the dynamic graph updates.

* Click the switches "Toggle Time" and "Toggle Build History" to display
  additional data.

## Changelog
See
[CHANGELOG.md](https://github.com/jenkinsci/yet-another-build-visualizer-plugin/blob/master/CHANGELOG.md)
for release details.

## Contributing
See
[CONTRIBUTING.md](https://github.com/jenkinsci/yet-another-build-visualizer-plugin/blob/master/CONTRIBUTING.md)
for details.

## Plug-in Dependencies
This plugin is dependent on the *Downstream Build Cache* plugin, which keeps
tracks of the upstream -> downstream relationship of all builds in Jenkins.

