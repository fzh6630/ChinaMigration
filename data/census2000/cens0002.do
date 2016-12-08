log using d:\yxia6\census00\cens0002.log, replace
set more off
set mem 180m

* cens0002.do 09/03/2009
* translate chinese labels into English 

use d:\yxia6\census00\cens0001

label var district "prefecture city code"
label var htype "household type"
label var h61 "male,temporarily residing in the local, leaving the domicile of Hukou,<0.5yr"
label var h62 "female,temporarily residing in the local, leaving the domicile of Hukou,<0.5yr"
label var h9 "the number of housing"
label var h10 "constructional area of house, square metres"
label var h11 "residential use"
label var colive "are there any other cotenants"
label var h13 "when the building was built"
label var h14 "the number of the building layers"
label var h15 "building's exterior wall material"
label var h16 "is there any kitchen in the house"
label var h17 "main cooking fuel"
label var h18 "whether drink tap water or not"
label var h19 "are there any balneal facilities in the house"
label var h20 "is there any toilet in the house"
label var h21 "sourse of housing"
label var h22 "the costs of the housing acquisition "
label var h23 "monthly rental fee"
label var ethnic "folk"
label var r61 "household registration status: place of residence and place of registration" 
label var r62 "household registration status: registered site"
label var r63 "household registration status: registered in the other provinces"
label var emp "last week, whether is engaged in labor for more than one hour with payment" 
label var empday "days:length of work last week"
label var ruemp "the situation of people who are not employed"
label var r261 "(15 to 50 years old women), fertility status, past yr"
label var r262 "(15 to 50 years old women), month of child birth, past yr"
label var r263 "(15 to 50 years old women) reproductive status over the previous year,infant gender"
label var r264 "(15 to 50 years old women) month of giving birth to a second"
label var r265 "(15 to 50 years old women) infant gender,second child birth status over the previous year"
label var r251 "(15 to 50 years old women) fertility: # male"
label var r252 "(15 to 50 years old women) fertility: # female"
label var r253 "(15 to 50 years old women) fertility: # male alive now"
label var r254 "(15 to 50 years old women) fertility: # female alive now"
label var rururban "current address,1=urban,2=township,3=rural"

******************************************

compress

sort hhid pid
sum
d
save d:\yxia6\census00\cens0002, replace
log close

