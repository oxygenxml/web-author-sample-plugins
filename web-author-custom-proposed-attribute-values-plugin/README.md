# web-author-custom-proposed-attribute-values-plugin

A sample plugin that customize the proposed attributes (content completion) for an attribute.
More concrete, it sets "Oxygen XML Editor" and "Oxygen XML Author" as the proposed values for the "product" attribute by implementing the SchemaManagerFilter API [0].
Note that SchemaManagerFilter implementation can be specified only through ExtensionsBundle.createSchemaManagerFilter().

Other ways that can customize proposed attribute values:
1. by using a framework-level "cc_config.xml" file [1]
1. by using a Subject Scheme Map for DITA ([2], [3])

[0] https://www.oxygenxml.com/InstData/Editor/SDK/javadoc/ro/sync/contentcompletion/xml/SchemaManagerFilter.html
[1] https://www.oxygenxml.com/doc/versions/24.1/ug-editor/topics/configuring-content-completion-proposals.html
[2] https://www.oxygenxml.com/dita/1.3/specs/archSpec/base/subject-scheme-maps-and-usage.html
[3] https://www.oxygenxml.com/dita/1.3/specs/archSpec/base/binding-controlled-values-to-attribute.html#concept_fpj_jlx_54__example-binding-values