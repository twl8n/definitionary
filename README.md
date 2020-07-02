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

Given that only the most popular single word is used for a given definition+language, we may only supply one word.


#### SQLite notes

SQLite doesn't seem to support named parameters in the true sense. Indexed params are fine, but named params
are really just indexed params. There seems to be no native SQL to create something akin to a hash map for
named params. The SQL libraries for many programming languages overcome this by supporting high level params,
but that doesn't seem to be a native SQLite feature.

```sql
create table if not exists foo (
        id integer primary key,
        myval text);

insert into foo (myval) values (?), (12), (13);
insert into foo (myval,id) values (?1,?2), (14,13),(15,14);
```


#### http request map format

```
{:ssl-client-cert nil,
 :protocol "HTTP/1.1",
 :remote-addr "0:0:0:0:0:0:0:1",
 :params {"action" "foo"},
 :headers
 {"upgrade-insecure-requests" "1",
  "accept"
  "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
  "connection" "keep-alive",
  "accept-encoding" "gzip, deflate",
  "cache-control" "max-age=0",
  "user-agent"
  "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.14; rv:77.0) Gecko/20100101 Firefox/77.0",
  "accept-language" "en-US,en;q=0.5",
  "host" "localhost:8080"},
 :server-port 8080,
 :content-length nil,
 :form-params {},
 :query-params {"action" "foo"},
 :content-type nil,
 :character-encoding nil,
 :uri "/app",
 :server-name "localhost",
 :query-string "action=foo",
 :body
 #object[org.eclipse.jetty.server.HttpInput 0x28fd6bbe "org.eclipse.jetty.server.HttpInput@28fd6bbe"],
 :multipart-params {},
 :scheme :http,
 :request-method :get}
```
