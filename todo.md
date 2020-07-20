
- 2020-07-08 Add auth and deploy on server. What about https on Lightsail?

https://gist.github.com/karanth/8633258

https://github.com/remvee/ring-basic-authentication

https://github.com/cemerick/friend

https://stackoverflow.com/questions/13702003/how-to-implement-user-authentication-using-clojure-liberator

Also, what about routes?

+ 2020-07-06 Need to be able to save existing id, new lang. 

How to keep from overwriting existing id,lang accidentally? UI needs a "change lang" button or something.

Can't change the lang of a defini, but that's not "save-as" or something.

Disable lang change on edit. Enable only on create.

+ 2020-07-02 Need word+defini create, edit. 

Choose lang.
Search for existing, if found display edit link(s).
Add new word + def

Search can start as SQL % match.

Might be nice to have list of words "starting with". Click links to edit.

Will need report of defini that don't have words+def phrase in all languages.

+ (mostly done, based on expense-mgr ring server) 2020-06-18 create a server back end, almost certainly in clojure. Maybe babashka?

Simplest to just `lein uberjar` and run the jar file.

Works to use my clojure interpreter, clojint, but I don't see the point in that.

- 2020-06-18 create end points

* add gender-specific. Can it be algorithmic?
* image+overlay data save one (or save all?)
* image+overlay retrieve all
* retrieve definitionary data to fill text in overlay
* lookup id for word/phrase when creating overlay?

- 2020-06-18 Need definitionary word/defintion/language create, insert, update, delete UI end points and related HTML, etc.


