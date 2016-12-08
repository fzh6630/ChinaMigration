set more off
log using cipi.log, replace

* cipi.do Feb 2013
* estimate provincial compound investment price index - cipi

tempfile t1 t2 t3 t4

* input nominal investment data
insheet using ni.csv, names clear
qui destring, replace
d
sum
reshape long y, i(provcode) j(year)
ren y ni
la var ni "nominal investment, 1952-2004"

* years 1952-2004
drop if year<1952

* combine sichuan (51) and chongqing (50)
tab provcode
sort year provcode
list year provcode ni if provcode==50 | provcode==51, sep(0)
replace ni=0 if ni==. & provcode==50
replace ni=ni+ni[_n-1] if provcode==51
list year provcode ni if provcode==50 | provcode==51, sep(0)
drop if provcode==50

sort provcode year
save `t1'
d
sum

* input real capital stock
insheet using capital_52_05.csv, names clear
* years 1952-2004
drop if year==2005
qui destring, replace
tab provcode

sort provcode year
merge 1:1 provcode year using `t1'
drop _merge

* region
gen region=2 
replace region=1 if provcode==11 | provcode==12 | provcode==21 | provcode==31 | provcode==32 | provcode==33 | provcode==35 | provcode==37 | provcode==44
replace region=3 if provcode==15 | provcode>=52 & provcode<=65
la var region "3 regions 1 east 2 central 3 west"
tab region

sort provcode year
compress
save `t2'
d 
sum

* inspect missing ni
sort provcode year 
tab year provcode if ni==.

*=======================================
* estimate ipi using complete data first
* 23 proinces (7 have missing)
drop if provcode==36 | provcode==44 | provcode==46 | provcode==51 | provcode==54 | provcode==63 | provcode==64

sort provcode year
by provcode: gen ri=ktotal-0.904*ktotal[_n-1] if _n>1
gen cipi=ni/ri
replace cipi=1 if year==1952
sum cipi 
sort provcode year
list provcode year ktotal ni ri cipi if provcode==32, sep(0)
list provcode year ktotal ni ri cipi if provcode==53, sep(0)
list provcode year ktotal ni ri cipi if provcode==21, sep(0)
list provcode year ktotal ni ri cipi if provcode==34, sep(0)
list provcode year ktotal ni ri cipi if provcode==35, sep(0)
list provcode year ktotal ni ri cipi if provcode==42, sep(0)

sort provcode year
save `t3'
d
sum

* top-coded region mean cipi
replace cipi=7 if cipi>7 & cipi<.
sort region year
egen rmcipi=mean(cipi), by(region year)
la var rmcipi "7-top-coded regional mean cipi by year"
sum rmcipi cipi
keep region year rmcipi
sort region year
drop if region==region[_n-1] & year==year[_n-1]
sort region year
list region year rmcipi , sepby(region)
save `t4'

use `t2', clear
sort region year
merge m:1 region year using `t4'
drop _merge
keep if provcode==36 | provcode==44 | provcode==46 | provcode==51 | provcode==54 | provcode==63 | provcode==64

sort provcode year
by provcode: gen ri=ktotal-0.904*ktotal[_n-1] if _n>1
gen cipi=ni/ri
replace cipi=1 if year==1952
replace cipi=rmcipi if cipi==.
la var ri "real investment, 1952 price"
la var cipi "compound investment price index from 1952 to t"
sum cipi
list provcode year ktotal ri ni rmcipi cipi if provcode==44, sep(0)
list provcode year ktotal ri ni rmcipi cipi if provcode==54, sep(0)
* huge discrepancies between Zhang's and ni 1988 and later
* replace with western region mean for all years for 54
replace cipi=rmcipi if provcode==54
drop rmcipi 

* append all provinces
append using `t3'
sort region year
merge m:1 region year using `t4'
drop _merge

* top-code provcode=53,34 with western region mean
replace cipi=rmcipi if cipi>rmcipi & (provcode==53 | provcode==34)
sum cipi
list provcode year ktotal ri ni rmcipi cipi if provcode==34, sep(0)
list provcode year ktotal ri ni rmcipi cipi if provcode==53, sep(0)

la var ktotal "real total capital stock, 1952 price"
la var provcode "official province code"
la var year "year"
keep provcode year ktotal cipi 
order provcode year ktotal cipi
compress
sort provcode year
save cipi, replace
d
sum

log close

