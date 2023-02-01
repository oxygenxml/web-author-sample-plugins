Web Author Table Computed Values
================================

Plugin that allows you to add automatically-computed values in a table cell.

Demo
----

You can see a demo of this plugin in action in the [sample document](./samples/elections.dita):

![Demo](./samples/elections.gif)

[Try it yourself](https://www.oxygenxml.com/oxygen-xml-web-author/app/oxygen.html?url=gitgh%3A%2F%2Fhttps%253A%252F%252Fgithub.com%252Foxygenxml%252Fweb-author-sample-plugins%2Fmaster%2Fweb-author-table-cell-formulas%2Fsamples%2Felections.dita)


Usage
-----

Once you installed the plugin, to add such a computed value, add the following processing-instruction in the XML source:

    <?computed-value formula="[some-xpath-expression]"?>

The XPath formula is resolved relative to the processing-instruction. The example below, computes the sum of all cells 
in the column where the processing-instruction is located (it assumes that the cell that contains the PI does not have
other content than the PI):

    let 
      $cell := ./ancestor-or-self::stentry,
      $table := ./ancestor-or-self::simpletable,
      $column :=  count(./ancestor-or-self::stentry/preceding-sibling::*) + 1
    return sum($table/strow/stentry[$column][text() != ''])

To make it easy to add such a processing-instruction, you can 
[configure a toolbar button](https://www.oxygenxml.com/doc/versions/25.0.0/ug-waCustom/topics/wa_inserting_xml_elements.html#wa_inserting_xml_elements__section_bzb_vsy_dlb) 
to insert it.