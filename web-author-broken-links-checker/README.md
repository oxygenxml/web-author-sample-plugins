Web Author Broken Links Checker Plugin
=======================================

Sample plugin that reports broken `href`/`conref` links (targets that cannot
be resolved) as validation errors in the Web Author "Validation" panel,
using the `ValidationProblemsFilter` extension point.

Limitations: only `href` and `conref` are checked (not `keyref`/`conkeyref`);
absolute `http(s)://`/`ftp://`/`mailto:` links are skipped; for a target like
`topic.dita#some-id` only the reachability of `topic.dita` is checked, the
`#some-id` fragment is not resolved against the target's IDs.
