#### Definitionary inverse dictionary

Demo and API for the inverse dictionary based on unique definitions.

The inverse dictionary uses definitions as the primary key. Each concept is assigned a unique id (numeric at
this time). People editing the definitionary must decide how finely to quantize meanings, but in general, very
fine distinctions are good. Each definition has text describing it in several languages. Definition text in
each language is linked to words or phrases in that language. Related words or phrases are ordered by how
commonly that word is used for the given definition.

Definitionary documents called "Language Independent Documents" or LIDs are created by simply stringing
together definitions. Subject-verb-object order is left to the human reader to interpret. While potentially
confusing, people are smart, and are perfectly capable of gleaning meaning from "sentences" with the wrong
order. 

Single words, for example used to label diagrams, are actually definitions as well. The "word" displayed is
simply the lookup of the most common word in the selected output language for the unique definition. Ideally,
the UI includes some mouse-over or similar feature for the text that reveals the text of the definition in the
selected language.


