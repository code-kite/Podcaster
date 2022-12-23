# Podcaster

Podcaster app is for creating editing posting podcast.

## Notes
  - The latest code will be available in master branch
  - Updated minimum sdk version to 19 as there was a need to implement Autoclosable interface which is available in 19 and above

## Improvements
  - Leak when sso login process completes
  - Leak in SegmentRecording Service when app closes. Though development is done as described here [https://developer.android.com/guide/components/bound-services#Binder] but still leak is encountered. Can support binding via Messenger
  - Figure out a way to implement parallel execution without coroutine scope in StopWatch

