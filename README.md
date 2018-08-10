# linkRotDetector
A Java based program that can grab all external links from a Wikipedia page and then analyze the links for 404 or "soft-404" errors.

# Basis for algorithm
The program is an implementation of the algorithm described in Sic Transit Gloria Telae:∗Towards an Understanding of the Web’s Decay
which can be found here: http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.1.9406

# APIs required
This project now uses Maven, the dependencies it uses are Jsoup and Apache.commons.lang.

# Usage of the program
java linkRotDetector [Wikipedia article address]

# Effects
The program will grab all the citation and reference links that lead to sites outside of Wikipedia, analyze them for link rot and then output a Report.txt in the root
of what ever folder you store the program in. It will always overwrite a previous Report.txt and will create a new one
if it does not exist.

# Weaknesses
Some website's text is not always picked up by Jsoup's link.text() function. Be aware that a false positive is possible.
Some websites purposefully make it difficult for bots to analyze the content of their pages and also do not
return propper hard 404s which makes it hard for the program to tell a live link from a dead link.
These sites can often be mistaken for dead links in this case.

The algorithm also makes the assumption that host urls
cannot be 404 links. See the cited document under the Basis for algorithm section for more information o