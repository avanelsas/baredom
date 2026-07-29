# Changelog

## 0.2.1

Projects reconstructed state only while replaying, not on every live event. The live view and its animations are left untouched during normal use.

## 0.2.0

Reconstructs and projects multiple server-resource independently. A page with several server-resources now replays each on its own timeline.

## 0.1.0

First release. A time-travel replay debugger for BareBuild apps. It records the event log and reconstructs any past state by replaying the pure `step`, then re-projects it onto the live components. Ships a draggable dock with transport controls, a live-updating event timeline, and a request and response data pane. Published to Clojars.
