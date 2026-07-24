# web-author-block-readonly-topic

Sample plugin that makes DITA topics read-only and shows a **custom message**
when the user tries to edit them, instead of Oxygen's built-in
*"Read-only content: Use \"Edit Reference\" action from the contextual menu."*

Topics are marked read-only with `outputclass="readonly"` on the
corresponding `topicref`. The plugin installs an
[`AuthorDocumentFilter`](https://www.oxygenxml.com/InstData/Editor/SDK/javadoc/ro/sync/ecss/extensions/api/AuthorDocumentFilter.html)
on every editing session (via a `WorkspaceAccess` extension) and rejects the
editing operations (`insertText`, `insertFragment`, `insertNode`, `delete`,
`deleteNode`, `multipleDelete`, `split`, `setAttribute`, `removeAttribute`)
whose target lies inside such a topic, showing a custom warning message.

## Why not just use CSS `-oxy-editable:false`?

The message *"Use Edit Reference action from the contextual menu."* is produced
by Oxygen **core** (assembled from the `Read_only_content` and
`Use_edit_reference_action` messages) and is **not** exposed through CSS, the
framework, or the SDK.

When content is made non-editable with the CSS `-oxy-editable:false`, the core
rejects the edit — and shows that built-in message — **before** any
`AuthorDocumentFilter` runs. As a result, a document filter cannot change or
suppress that message while `-oxy-editable:false` is active.

Therefore this plugin enforces read-only **itself**. Do **not** combine it with
`-oxy-editable:false`. If you want a visual cue for the read-only topics, use a
plain CSS rule (no `-oxy-editable`), e.g. in your framework CSS:

```css
topicref[outputclass="readonly"] {
    background-color: #f3f3f3;
    opacity: 0.85;
}
```

## Configuration

Constants at the top of `ReadOnlyTopicMessagePluginExtension`:

- `READONLY_ATTR` / `READONLY_VALUE` — the attribute and token that mark a topic
  read-only (`outputclass` / `readonly`).
- `CUSTOM_MESSAGE` — the warning shown to the user; set it to `null` to reject
  edits silently (no message).

## Build

```
mvn clean package
```

The packaged plugin is produced under `target/`. The pre-built JAR is also
committed under `lib/` for convenience. Set `<oxygen.sdk.version>` in `pom.xml`
to match your Web Author version.
