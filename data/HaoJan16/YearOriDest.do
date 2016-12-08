cd /Users/lhao/Documents/abm_2015/raw_csv_work/Jan16
log using YearOriDest.log, replace
set more off

* YearOriDest.do	Jan 2016

* agents to start with ages 11-44 in 1995 who were 16-49 in 2000 
* save YearOriDest.dta for individual data for all years 
* beyond lf ages each year
* merge in hhid from which get pv20000 (fix the existing spv2000)

tempfile h t s
set linesize 255

*================
* individual data
*================
* hhid 
use /Users/lhao/Documents/abm_2015/raw_csv_work/Nov15/hhid
gen pv2000=int(hhid/1000000)
tab pv2000
sort pid
save `h'

use /Users/lhao/Documents/abm_2015/raw_csv_work/Nov15/csv1_d2
keep pid provhk spv* rup* age male edyr 

* exclude 13 individuals migrated before 1995 & age<16 in 1995 (age<21 in 2000)
tab rup1995 if age<21
drop if (rup1995==2 | rup1995==3) & age<21

* age range 16-49 in 2000, i.e., 11-44 in1995
keep if age>=16 & age<=49
sum age

sort pid
merge 1:1 pid using `h'
drop if _merge==2
drop _merge
ren pv2000 spvcheck
ren provhk spvhk
# delimit ;
recode spvcheck spvhk
(	11	=	1	)
(	12	=	2	)
(	13	=	3	)
(	14	=	4	)
(	15	=	5	)
(	21	=	6	)
(	22	=	7	)
(	23	=	8	)
(	31	=	9	)
(	32	=	10	)
(	33	=	11	)
(	34	=	12	)
(	35	=	13	)
(	36	=	14	)
(	37	=	15	)
(	41	=	16	)
(	42	=	17	)
(	43	=	18	)
(	44	=	19	)
(	45	=	20	)
(	46	=	21	)
(	50	=	22	)
(	51	=	23	)
(	52	=	24	)
(	53	=	25	)
(	54	=	26	)
(	61	=	27	)
(	62	=	28	)
(	63	=	29	)
(	64	=	30	)
(	65	=	31	)
;
# delimit cr
tab1 spvcheck spvhk

tab rup1995

forval i=1995/2000 {
ren rup`i' mig`i'
recode mig`i' 1=0 2 3=1
la var mig`i' "migration status 1 yes 0 no"
}

save `t', replace
d
sum

*==========================
* spv and spvhk consistence
* spvcheck in 2000 has 31 provinces (chongqing and sichuan)
tab spv2000 spvcheck if spvcheck<16
tab spv2000 spvcheck if spvcheck>=16

* fix spv2000
drop spv2000
ren spvcheck spv2000


* migrants in stock of each year
gen m1995=mig1995
egen m1996=rowmax(mig1995 mig1996)
egen m1997=rowmax(mig1995 mig1996 mig1997)
egen m1998=rowmax(mig1995 mig1996 mig1997 mig1998)
egen m1999=rowmax(mig1995 mig1996 mig1997 mig1998 mig1999)
egen m2000=rowmax(mig1995 mig1996 mig1997 mig1998 mig1999 mig2000)
tab1 m1995-m2000

* fix spvhk of nonmigrants
replace spvhk=spv2000 if m2000==0
save `t', replace

* break up spvhk=23 if m2000=1
keep if m2000==1 & spvhk==23
sort pid
gen random=runiform()
sort random
replace spvhk=22 if random<=.2365
drop random
save `s'
use `t', clear
drop if m2000==1 & spvhk==23
append using `s'
tab spvhk

* fix spv1995 - spv1999 (5 years)
forval i=1995/2000 {
  replace spv`i'=spvhk if m`i'==0
  replace spv`i'=spv2000 if m`i'==1
}
tab1 spv1995 spv1996 spv1997 spv1998 spv1999 spv2000

* check spv2000 within-hhid consistence
* spvhk are consistent for the majority (spouses could have different spvhk)
sort hhid
gen gapspv=spv2000-spv2000[_n-1] if hhid==hhid[_n-1] & hhid<.
gen gapspvhk=spvhk-spvhk[_n-1] if hhid==hhid[_n-1] & hhid<.
tab1 gapspv gapspvhk
drop gapspv gapspvhk
sum

* scramble Pid
* no missing id
sum hhid pid
sort spvhk mig1995
gen Pid=_n
sort hhid
save `t', replace

* scramble Hid
keep hhid
sort hhid
keep if hhid!=hhid[_n-1]
sort hhid
gen Hid=_n
sum
sort hhid
merge 1:m hhid using `t'
drop _merge

* disregard Hid of migrants in 2000 stock
replace Hid=. if m2000==1

* save wide-form data
saveold YearOriDest, replace
d
sum

*==========================
* aging, assuming no deaths
gen age1999=age-1
gen age1998=age-2
gen age1997=age-3
gen age1996=age-4
gen age1995=age-5
ren age age2000
sum age*

* long form
reshape long age spv m mig, i(Pid) j(year) 
sum
sort Hid Pid
save `t', replace

*=====================
* InitialAgentData.csv
*=====================
* 1995 micro data, including ages 11-15 nonmigrants
keep if year==1995
keep if age<45
keep Hid Pid spv spvhk age male edyr m
order Hid Pid spv spvhk age male edyr m
sum
tab m
tab spvhk spv if spv<16 & m==1
tab spvhk spv if spv>=16 & m==1

ren age Age
ren male Male
ren edyr YearOfEduc
ren m Migrated
ren Hid FamilyId
ren Pid PersonId

outsheet PersonId spv spvhk Age Male YearOfEduc Migrated FamilyId using InitialAgentData.csv, comma name replace

*====================
* EndingAgentData.csv
*====================
use `t', clear
* 2000 micro data
keep if year==2000
keep if age>=16 & age<=44
keep Hid Pid spv spvhk age male edyr m
order Hid Pid spv spvhk age male edyr m
sum
tab m
tab spvhk spv if spv<16 & m==1
tab spvhk spv if spv>=16 & m==1

ren age Age
ren male Male
ren edyr YearOfEduc
ren m Migrated
ren Hid FamilyId
ren Pid PersonId

outsheet PersonId spv spvhk Age Male YearOfEduc Migrated FamilyId using EndingAgentData.csv, comma name replace

*=====================
* YearOriDestCount.csv
*=====================
use `t', clear
* 6 years aggregates
keep if age>=16 & age<=44
keep year spv spvhk m
bys year spvhk spv: egen MigCount=total(m)
sort year spvhk spv
drop if year==year[_n-1] & spvhk==spvhk[_n-1] & spv==spv[_n-1]
outsheet year spvhk spv MigCount using YearOriDestCount.csv, comma name replace 

* annual mig counts check
bys year: egen ym=total(MigCount)
sort year
list year ym if year!=year[_n-1], sep(0)

log close
