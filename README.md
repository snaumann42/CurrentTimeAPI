Reading through [Haoyi's post](https://www.lihaoyi.com/post/12yearsofthecomlihaoyiScalaPlatform.html) 
resonated with me on how complex projects can get with Typelevel Projects. 
Some solutions don't need to be perfect, they just need to prove out a point 
or handle an initial client base.

I decided to build out this simplistic API using Cask, uPickle, Requests and uTest to see how it went.

I ended up swapping uPickle out for Circe due to how optional values were encoded/decoded in JSON. uPickle handles Options such that a None/Null is an empty List in JSON and Some(*someValue*) is a JSON list with one element.
