# Changelog

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
