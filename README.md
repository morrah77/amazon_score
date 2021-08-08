#Description

Amazon request score estimation service based on its autocomplete API

##Requirements

1. The score should be in the range [0 → 100] and represent the
estimated search-volume (how often Amazon customers search for that exact keyword).

2. score of 0 means that the keyword is practically never searched for, 100 means that this is
one of the hottest keywords in all of amazon.com right now

##Assumptions

Autocomplete API returns a set of strings representing the hottest 10 requests sorted by frequency (`by search-volume`) descending.

So, having the exact element in this set considering score = 100 based on Requirements#2

Having no exact element in the set, considering score 0 + weight(phrase, variants)

Knowing nothing about the real search volumes, let's assume the following regarding the case when there's no exact element in the set:

 - if the search key is a prefix of a search request, it's often requested `as is`, at least by mistake (for example, the user was looking for `phone case`, but tapped `Enter` key after the word `phone` by mistake, or the similar case for `large bag` -> `large`), so, we can consider it as a `rather frequent` one and give each prefix match a weight, let's say, K * (1/N), where N - a maximum size of set returned by API, 10 for the case of commonly used keys, less than 10 for rarely used keys, K - some coefficient, for ex. 0.4 for N = 10, 0.4 / (1 + 10 - N) for cases when N < 10,
 - if the search key is not a prefix of a request, it's hardly requested `as is`, so let's don't give such a case any weight
 - the estimated score might be calculated as a sum of the weights and led to the norm of 100

Also, having 10 seconds to make our estimation, we could perform several calls to the atocomplete endpoint with the key combined with some other words or splitted apart in the case of a complex key, but, honestly, having so few information about the exact search request volumes and/or autocomplete API algorithm, I see no reason in building more assumptions than it's done already.

###Questions

 - `a. What assumptions did you make?` - see above
- `b. How does your algorithm work?` - see above
- `c. Do you think the ( *hint ) that we gave you earlier is correct and if so - why?` - Yes and no. Keeping in mind a requirement of estimation the exact search request, it's insignificant when the key is in the list of requests returned by API (is in the top of the hottest search phrases), but when it comes to the case when the exact key doesn't present in the list, it theoretically could be searched `as is` in some cases (for ex. when it's a prefix of the search request). In the implemented algorithm the fact of sorting significancy isn't taken into account. 
- `d. How precise do you think your outcome is and why?` - Not precise at all :)))) Just a ton of assumptions. The API expected to use for search request estimation is not the best source of data. For example, Google search request statistics looks more promisable from the prospective of search request scoring, even when it comes to Amazon search (imho).


##Investigation

["smart",["smart watch","smart tv","smart light bulbs","32 in smart tv","samsung smart watch","amazon smart plug","owlet smart sock 3","smart plugs","smart sweets","smart lock"],[{},{},{},{},{},{},{},{},{},{}],[],"3VYP3ZRG5R3GO"]

["phone", ["portable phone charger","phone case","car phone holder mount","waterproof phone pouch","phone mount for car","iphone xs max phone case","phone stand","phone","phone holder","phone cases for iphone 11"],[{},{},{},{},{},{},{},{},{},{}],[],"..."]

["case",["iphone 12 pro max case","iphone 11 case","iphone 12 case","iphone xr case","case","casetify","casein protein","case of water","case for iphone 12 pro","casetify iphone 11"],[{},{},{},{},{},{},{},{},{},{}],[],"5PYBTOQJ0I3K"]

["phone case",["phone case","iphone xs max phone case","phone cases for iphone 11","iphone 12 pro max phone case","pop it phone case","waterproof phone case","phone cases for iphone 12","phone case for iphone xr","phone cases for iphone 11 pro max","phone case wallet"],[{},{},{},{},{},{},{},{},{},{}],[],"YMVY6T06527R"]

["charger",["portable phone charger","apple watch charger","portable charger","wireless charger","charger","charger block","chargers for ipads and phones","charger for iphone 11","charger plates","charger iphone"],[{},{},{},{},{},{},{},{},{},{}],[],"P1OTO0XS81BZ"]

["phone charger",["portable phone charger","phone charger","phone chargers iphone","apple iphone charger","solar phone charger","phone charger battery pack portable","wireless phone charger iphone","phone chargers for android","phone charger c type","phone charger block"],[{},{},{},{},{},{},{},{},{},{}],[],"8V8F6EDCXS50"]

["large bag",["large bag","large bags for women","bagel slicer for small and large bagels","large bags for storage","extra large bag with wheels","large bags for women tote","extra large bag","large bag clips","large bags of candy","large bag for travel"],[{},{},{},{},{},{},{},{},{},{}],[],"SPWD4ZBSI2S"]

["large",["bogg bag large beach tote","large dog bed","large dog crate","dehumidifiers for large room or basements","large mouse pad","large water bottle","large pop it","large mirror","large glue sticks","large wall clock"],[{},{},{},{},{},{},{},{},{},{}],[],"3EA8CF91FAXYN"]

["small",["small desk","small fan","small table","small trash bags","small wallet for women","small trash can","small cooler","small microwave","small backpack for women","small refrigerator"],[{},{},{},{},{},{},{},{},{},{}],[],"2WRNNJOCQM59W"]

["dozen",["cheaper by the dozen","cheaper by the dozen book","cheaper by the dozen dvd","half dozen egg cartons","dozen cousins beans","dozen cupcake containers","dozen roses","dozen a day","dozen cousins","dozen baseballs"],[{},{},{},{},{},{},{},{},{},{}],[],"55BBVSPBJK4V"]


##Prereqs

Requirements:

 - Java 1.8

 - Maven 3.6

##Build

It's a Maven project so just build it

```
mvn package [-DskipTests=true]
```

#Run

It's a Spring boot app by default being built with a provisioned Tomcat webserver, so just run it like a common Java archive

```
java -jar target/amazon_score-0.0.1-SNAPSHOT.war
```

##Test

###Manually

```
curl -iv -X GET -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" 'http://127.0.0.1:8080/estimate/?keyword=phone'

curl -iv -X GET -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" 'http://127.0.0.1:8080/estimate/?keyword=phone+charger'

```

###Run auto tests

```
mvn test
```

##Test results (2021-08-08)


####Request 'large bag':

```
curl -iv -X GET -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" 'http://127.0.0.1:8080/estimate/?keyword=large+bag'
```

Logs:

```
["large bag",["large bag","large bags for women","bagel slicer for small and large bagels","large bags for storage","extra large bag with wheels","large bags for women tote","extra large bag","large bag clips","large bags of candy","large bag for travel"],[{},{},{},{},{},{},{},{},{},{}],[],"1QHVK6YAU8QEO"]
```
Response:
```
200 '{"Keyword":"large bag","score":100}'
```

Summary: OK

####Request 'large':

```
curl -iv -X GET -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" 'http://127.0.0.1:8080/estimate/?keyword=large'
```

Logs:

```
["large",["bogg bag large beach tote","large dog bed","large dog crate","dehumidifiers for large room or basements","large mouse pad","large water bottle","large pop it","large mirror","large glue sticks","large wall clock"],[{},{},{},{},{},{},{},{},{},{}],[],"1EX135N7PRH5L"]
```
Response:
```
200 '{"Keyword":"large","score":32}'
```

Summary: OK

####Request 'dozen':

```
curl -iv -X GET -H "Authorization: Basic dXNlcjpwYXNzd29yZA==" 'http://127.0.0.1:8080/estimate/?keyword=dozen'
```

Logs:

```
["dozen",["cheaper by the dozen","cheaper by the dozen book","cheaper by the dozen dvd","half dozen egg cartons","dozen cousins beans","dozen cupcake containers","dozen roses","dozen a day","dozen cousins","dozen baseballs"],[{},{},{},{},{},{},{},{},{},{}],[],"2OSWD7G2IE7FB"]
```
Response:
```
200 '{"Keyword":"dozen","score":24}'
```

Summary: OK
