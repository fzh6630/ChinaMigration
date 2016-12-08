log using d:/yxia6/census00/cens0006.log, replace
set more off
set mem 600m


* merge census0005&yearbook_data2

use cens0005
sort province
merge province using yearbook_data2
drop _merge

sort provo5
merge provo5 using yearbook_data3
drop _merge

tab r9, nolabel





compress
sort pid
save d:/yxia6/census00/cens0006, replace
d
sum

log close
