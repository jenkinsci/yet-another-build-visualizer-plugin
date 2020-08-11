# Changelog

## v1.12
* **Feature**: Switch to hide upstream builds [JENKINS-63299]
* Fix: Security fixes [SECURITY-1940]

## v1.11
* **Feature:** Add /api/ endpoints for indicating if any builds are still ongoing
* Fix: Update downstream-build-cache dependency to v1.5.2 for performance fixes

## v1.10
* **Feature:** Rebuild cache asynchronously, allowing the Jenkins UI to be displayed *much* sooner when starting Jenkins.

## v1.9.8
* Fix: Updated downstream-build-cache-plugin to v1.4.1 for extra NPE-guards [JENKINS-60504]

## v1.9.7
* Fix: In rare situations concurrent modification exceptions was thrown if multiple child jobs was started at the same time

## v1.9.6
* Fix: Wrong build tree was displayed if the root build of the tree was a rebuild of previous build

## v1.9.5:
* Adjust size of the busy animation for history dots

## v1.9.4:
* Fix: Build flow failed to display on build page

## v1.9.3:
* Fix: Ensure Build Flow is dynamically refreshed

## v1.9.2:
* Fix: Null pointer exception for jobs without builds

## v1.9.1:
* Reverse order of the build history dots.

## v1.9:
* **Feature**: Switch to show build execution times.
* **Feature**: Switch to show dots indicating the build results of the 5 previous builds in each cell.
* **Feature**: Fullscreen Build Flow. Append "/yabv" to project/build page url.

## v1.8.1:
* Fix: Drop down menu disappeared after Build Flow update.
* Fix: Tweaked animation to make the text more readable.

## v1.8
* **Feature:** Try to resize Build Flow width to fit within page if possible.

## v1.7
* **Feature:** Automatically refresh Build Flow graph without reloading the full page (default interval: 10s).

## v1.6
* **Feature:** Ensure that job names are unique enough to be easily identifiable (especially helpful when rendering **multibranch pipeline** builds).

## v1.5.1
* Fix: NPE if CauseAction was missing from build.

## v1.5
* **Feature:** Include downstream queue items in the build flow graph.

## v1.4
* Use build.displayName instead of build.number when displaying the Build Flow.

## v1.3
* Fix: Missing border on visualized builds with state "not yet built".
* Fix: Builds currently in progress had non-clickable links.
* Raise ordinance on BuildFlowAction.

## v1.2
* First public release.
