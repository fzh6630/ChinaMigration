log using cens0001.log, replace
set more off
set mem 180m

* cens0001.do 09/13/2009
* translate chinese labels into English 

* Census 2000, no codebook, no var. lables, no value labels, no sampling method (how to choose HH?)
* HH and individual level variables
* hhid, pid, fid: HH ID, personal ID, family member ID within HH

* 41 empty records: dropped
* check & label variables
* all collective HH are transferred to be single-person HH, but info. based on HH is unclear
* "rururban": community type?
* problem: if there are 1 or 2 HHs, each HH has unique hhid; otherwise, the 2nd and above share the same HH id (hhid)
* colive (share the housing) cannot be used to differentiate co-habitated HHs
* too many rel==9?
* cens_chk01.dta: check data through excel and merge back (year of birth and marriage)
* cens_chk03.dat: check HH relationship

* 336,753 family HHs (1,139,504 individuals) + 8414 collective HHs (40,609 individuals)=345,167 HHs (1,180,111 individuals)

use census2000
sum hhid hhnumber pid province district

format pid %10.0f
lab var hhnumber "household ID inside district"
lab var hhid "household ID=province+district+hhnumber"
lab var pid "personal ID = hhid+individual ID (R1)"

lab var district "�ؼ��д���"

lab def yesno 1 "��" 2 "��"

* drop empty records: everthing is 0
drop if hhid==0 & prov==0

* individual & households
sum hhid

sort hhid
sum hhid if hhid!=hhid[_n-1]
tab h2 if hhid!=hhid[_n-1]
sum hhid if h2==1
sum hhid if h2==2

*******************************************
* check variables: add labels
tab h2
lab var h2 "����"
lab def h2 1 "��ͥ��" 2 "���廧"
lab value h2 h2
ren h2 htype

* people who have records (individual part)
tab1 h31 h32
tab h31 htype
tab h32 htype
lab var h31 "# male reg. in HH"
lab var h32 "# female reg. in HH"

* short-term out-migrants: should be registered (included in H3)
tab1 h41 h42
tab h41 htype
tab h42 htype
lab var h41 "# male moved out, <0.5 yr"
lab var h42 "# female moved out, <0.5 yr"

* long-term out-migrants: should not be reigrstered
tab1 h51 h52
tab h51 htype
tab h52 htype
lab var h51 "# male moved out, >=0.5yr"
lab var h52 "# female moved out, >=0.5yr"

* short-term in-migrants: should not be registered
tab1 h61 h62
tab h61 htype
tab h62 htype
lab var h61 "# male ��ס���أ��뿪������<0.5yr"
lab var h62 "# female ��ס���أ��뿪������<0.5yr"

tab1 h71 h72
lab var h71 "# male: born in past one yr"
lab var h72 "# female: born in past one yr"

tab1 h81 h82
lab var h81 "# male: died in past one yr"
lab var h82 "# female: died in past one yr"

******************************
* housing

lab var h9 "ס������"
lab var h10 "ס���������, ƽ��"

lab var h11 "ס����;"
lab def h11 1 "�����÷�" 2 "����������Ӫ�÷�"
lab value h11 h11

* h12: share-housing or not (why 0? --unusual residential place?)
tab h12
ren h12 colive
tab colive htype
lab var colive "��ס�����Ƿ���������ס��"

sum h13
recode h13 1=.
lab var h13 "����סլ����ʱ�䣬��"

lab var h14 "��������"
lab def h14 1 "ƽ��" 2 "���㼰����¥��" 3 "�߲㼰����¥��"
lab value h14 h14

lab var h15 "������ǽǽ�����"
lab def h15 1 "�ֽ�������ṹ" 2 "שʯ" 3 "שľ�ṹ" 4 "ľ���񡢲�" 5 "����"
lab value h15 h15

lab var h16 "ס�������޳���"
lab def h16 1 "��������ʹ��" 2 "����������������" 3 "��"
lab value h16 h16

lab var h17 "��Ҫ����ȼ��"
lab def h17 1 "ȼ��" 2 "��" 3 "ú̿" 4 "���" 5 "����"
lab value h17 h17

lab var h18 "�Ƿ���������ˮ"
lab value h18 yesno

lab var h19 "ס��������ϴ����ʩ" 
lab def h19 1 "ͳ�ƹ���ˮ" 2 "��ͥ��װ��ˮ��" 3 "����" 4 "��"
lab value h19 h19

lab var h20 "ס�������޲���"
lab def h20 1 "����ʹ�ó�ˮʽ" 2 "�ھӺ��ó�ˮʽ" 3 "����ʹ������ʽ��" 4 "�ھӺ�������ʽ��" 5 "����"
lab value h20 h20

lab var h21 "ס����Դ"
lab def h21 1 "�Խ�ס��" 2 "������Ʒ��" 3 "���򾭼����÷�" 4 "����ԭ����ס��" 5 "���޹���ס��" 6 "������Ʒס��" 7 "����"
lab value h21 h21

lab var h22 "����ס������"
lab def h22 1 "<1��" 2 "1-2��"  3 "2-3��"  4 "3-5��"  5 "5-10��"  6 "10-20��"  7 "20-30��"  8 "30-50��"  9 ">50��"
lab values h22 h22

lab var h23 "���ⷿ����"
lab def h23  1 "<20" 2 "20-50" 3 "50-100" 4 "100-200"  5 "200-500"  6 "500-1000"  7 "1000-1500" 8 "1500-2000"  9 ">2000"
lab values h23 h23

*******************
sort hhid
qui by hhid: gen fsize=_N

* colive=0: might live in some unusual places, under construction? (lots of housing info. is "0")
preserve
keep if colive==0
drop if htype==2
sum hhid h9 h10 h11 if hhid!=hhid[_n-1]
tab fsize if hhid!=hhid[_n-1]
sum h* if hhid!=hhid[_n-1]
restore

* co-live HH: not too many non-relatives -->cannot be used to identify different HHs
tab r2 colive
tab r2 colive if htype==1
tab r2 colive if htype==2

*************************************
preserve
drop r*

sort hhid
keep if hhid!=hhid[_n-1]

* H3 (# of people registered) is consistent with # of recodes
recode h31 h32 (.=0)
gen np2=h31+h32
sum hhid h31 h32 fsize np2 if fsize!=np2

* H4 (*_ms: short-term emmigrant) were supposed to register --> should be included in records
* -->ignore (don't need this variable)
sum hhid if h41>h31 & h41<.
tab htype colive if h41>h31 & h41<.
tab h41 h31 if h41>h31 & h41<.
list hhid h31 h32 h41 h42 htype colive if h41>h31 & h41<.

sum hhid if h42>h32 & h42<.
tab htype colive if h42>h32 & h42<.
tab h42 h32 if h42>h32 & h42<.
list hhid h31 h32 h42 h41 if h42>h32 & h42<.

* H5: emigrant more than 0.5yrs (how many HHs)
sort hhid
tab htype if h51>0 & h51<. | h52>0 & h52<.

* temporary residents, left hometown for less than 0.5yrs
tab htype if h61>0 & h61<. | h62>0 & h62<.

sum h7* h8*
restore

********************************
* family: everybody in the HHs
ren r1 fid
lab var fid "R1, individual ID indside household"

sort hhid fid
gen chk=fid-fid[_n-1] if hhid==hhid[_n-1]
tab chk
drop chk

ren r2 rel
tab rel
lab def rel 0 "HH head" 1 "spouse" 2 "child" 3 "parents" 4 "par-in-law" 5 "grandpar" 6 "chd-in-law" 7 "grandchd"  8 "sibs" 9 "others"
lab values rel rel
lab var rel "rel. with HH header"
tab rel htype

ren r3 male
recode male 2=0
tab male

* check age: survey was conducted on Nov. 1st, 2000
ren r41 birthy
tab birthy
ren r42 birthm
tab birthm
sum birth*
gen chk=2000-birthy
replace chk=chk-1 if birthm==11 | birthm==12
sum chk
gen chk2=chk-age
tab chk2
drop chk*

ren r5 ethnic
lab var ethnic "����"
lab def eth 1 "����"  2 "�ɹ���"  3 "����"  4 "����"  5 "ά�����"  6 "����"  7 "����"  8 "׳��"  9 "������"  10 "������" ///
            11 "����" 12 "����"  13 "����"  14 "����" 15 "������" 16 "������" 17 "��������"       18 "����"  19 "����" ///
            20 "������"  21 "����"  22 "���"  23 "��ɽ��"  24 "������"  25 "ˮ��"  26 "������"  27 "������"  28 "������"  ///
            29 "�¶�������"  30 "����"  31 "���Ӷ���"  32 "������"  33 "Ǽ��"  34 "������"  35 "������"  36 "ë����"  ///
            37 "������"     38 "������"  39 "������"  40 "������" 41 "��������" 42 "ŭ��" 43 "���α����" 44 "����˹��" ///
            45 "���¿���"   46 "������"  47 "������"  48 "ԣ����" 49 "����" 50 "��������" 51 "������"     52 "���״���"  ///
            53 "������"    54 "�Ű���"   55 "�����"  56 "��ŵ��"         97  "����δʶ������"     98 "��������й���"
lab value ethnic eth
tab ethnic

* Hukou (R6)
* r62=0: skip the question (r61=1,4,5)
lab var r61 "���ڵǼ�״������ס����Ǽǵ�"
lab def r61   1 "��ס����/�֣������ڱ���/��" ///
               2 "��ס����/��>���꣬��������/��" ///
               3 "��ס����/��<���꣬�뿪���ڵǼǵ�>����" ///
               4 "��ס����/�֣����ڴ���"  ///
               5 "ԭס����/�֣�����"
lab values r61 r61
tab r61

lab var r62 "���ڵǼ�״�����Ǽǵ�"
lab def r62 1 "���أ��У�������"  2 "���أ��У�������"  3 "���أ��У������ֵ�"  4 "������������"  5 "������������"  ///
              6 "�����������ֵ�"    7 "��ʡ�����أ��У�������"    8 "ʡ��"
lab values r62 r62
tab r62
tab r62 r61

lab var r63 "���ڵǼ�״�����Ǽǵأ���ʡ"

* r7=0: no hukou
tab r7
tab r61 r7
lab def r7  1 "ũҵ����" 2 "��ũ����"
lab values r7 r7
lab var r7 "Hukou status: 0=no hukou 1=agr. 2=urban"

lab def r81 1 "���ء��С���"  2 "��ʡ���ء��С���"  3 "��ʡ"
lab values r81 r81
tab r81

tab r82
lab var r81 "where was born 1=cur. county 2=cur. prov 3=oth prov"
lab var r82 "where was born: (other)prov."

lab var r9 "which yr move here"
lab def r9 1 "������һֱס������ֵ�" 2 "1995.10.31��ǰ" 3 "1995.11.1-12.31" 4 "1996��"  5 "1997��" 6 "1998��"  7 "1999��"  8 "2000��"
lab values r9 r9
tab r9

* inconsistent between R6 & R9: correct R6 based on R9 (including less than half-year-old baby)
tab r61 r9
tab age r9 if r61==3 & r9<8
tab birthm if r61==2 & birthy==2000
replace r61=2 if r61==3 & r9<8

* R10, r101: 0=skip, 3=other province
* r102, r103, r104: all people came from other counties (including same prov.)
lab values r101 r81
tab r101
sum r102 r103 r104

lab var r101 "move from where, 1=inside county/city 2=oth county/city 3=oth prov."
lab var r102 "move from where, province"
lab var r103 "move from where, prefecture"
lab var r104 "move from where, county"

* R11, 0=skip
lab def r11	1 "��" 2 "��ľ�ί��" 3 "��Ĵ�ί��" 4 "�ֵ�"
lab values r11 r11
tab r11
tab r11 r101
lab var r11 "R11: type of community moved out"

lab def r12 1 "�񹤾���" 2 "��������" 3 "����¼��" 4 "ѧϰ��ѵ" 5 "��Ǩ���" 6 "����Ǩ��" 7 "��Ǩ����" 8 "Ͷ�׿���" 9 "����"
lab values r12 r12
tab r12
lab var r12 "R12 why move"

* 0: be here 5 yrs ago, or yonger than 5-yrs-old
lab def r131 1 "ʡ��" 2 "ʡ��"
lab values r131 r131
tab r131
lab var r131 "where 5 yrs ago 2=oth prov."
lab var r132 "where 5 yrs ago, province"

* for people above 6-yrs-old
tab r14
ren r14 literate
tab age if literate==0
recode literate 0=. 2=0
lab var literate "0=illiterate"

ren r151 educ
tab educ literate
tab literate if educ==.
lab def educ 1 "no sch" 2 "Sao Mang" 3 "ele." 4 "junior sch" 5 "high sch" 6 "ZhongZhuan" 7 "DaZhuan" 8 "college" 9 "graduate sch"
lab values educ educ
lab var educ "1=no sch 2=SaoMang 3=ele. 4=junior 5=high"

ren r152 ceduc
tab1 educ ceduc
recode ceduc 2=0
lab var ceduc "continous educ. 1=yes"

ren r16 feduc
lab def feduc 1 "��У" 2 "��ҵ" 3 "��ҵ" 4 "�ѧ" 5 "����"
lab values feduc feduc
tab feduc
lab var feduc "finish schooling?"

ren r17 emp
lab def emp 1 "��" 2 "��ְ�ݼ�,ѧϰ,��ʱͣ���򼾽���Ъҵδ����" 3 "δ���κι���"
lab values emp emp
tab emp
lab var emp "R17, �����Ƿ������һСʱ������������Ͷ�"

ren r18 empday
tab empday
lab var empday "# dyas worked last wk"

sum r19 r20
ren r19 industry

#delimit ;
lab def industry
0    "no job"
11   "��ֲҵ"
19   "����ũҵ"
20   "��ҵ"
31   "������������ҵ"
32   "��������ҵ"
33   "����ҵ"
39   "��������ҵ"
41   "������ҵ"
42   "��ˮ��ҵ"
51   "ũҵ����ҵ"
52   "��ҵ����ҵ"
53   "������ҽ����ҵ"
54   "��ҵ����ҵ"
59   "����ũ���֡����������ҵ"
61   "ú̿����ҵ"
62   "ú̿ϴѡҵ"
71   "��Ȼԭ�Ϳ���ҵ"
72   "��Ȼ������ҵ"
73   "��ҳ�ҿ���ҵ"
81   "�����ѡҵ"
82   "������ɫ�������ѡҵ"
91   "����ɫ�������ѡҵ"
93   "����ɫ�������ѡҵ"
95   "��������ѡҵ"
96   "ϡ��ϡ���������ѡҵ"
101   "��ɰʯ����ҵ"
102   "��ѧ���ѡҵ"
103   "����ҵ"
109   "�����ǽ������ѡҵ"
110   "�������ѡҵ"
121   "ľ�Ĳ���ҵ"
122   "��Ĳ���ҵ"
131   "��ʳ�����ϼӹ�ҵ"
132   "ֲ���ͼӹ�ҵ"
133   "����ҵ"
134   "���׼����൰��ӹ�ҵ"
135   "ˮ��Ʒ�ӹ�ҵ"
136   "�μӹ�ҵ"
139   "����ʳƷ�ӹ�ҵ"
141   "��㡢�ǹ�����ҵ"
142   "����Ʒ����ҵ"
143   "��ͷʳƷ����ҵ"
144   "������Ʒҵ"
145   "��ζƷ����ҵ"
149   "����ʳƷ����ҵ"
151   "�ƾ������Ͼ�����ҵ"
152   "����������ҵ"
155   "�Ʋ�ҵ"
159   "������������ҵ"
161   "��Ҷ����ҵ"
162   "��������ҵ"
169   "�����̲ݼӹ�ҵ"
171   "��άԭ�ϳ����ӹ�ҵ"
172   "�޷�֯ҵ"
174   "ë��֯ҵ"
176   "���֯ҵ"
177   "˿���֯ҵ"
178   "��֯Ʒҵ"
179   "������֯ҵ"
181   "��װ����ҵ"
182   "��ñҵ"
183   "��Ьҵ"
189   "������ά��Ʒ����ҵ"
191   "�Ƹ�ҵ"
192   "Ƥ����Ʒ����ҵ"
193   "ëƤ���Ƽ���Ʒҵ"
195   "��ë���ޣ�����Ʒҵ"
201   "��ġ�ľƬ�ӹ�ҵ"
202   "���������ҵ"
203   "ľ��Ʒҵ"
204   "���١��ء�����Ʒҵ"
211   "ľ�ƼҾ�����ҵ"
212   "���ټҾ�����ҵ"
213   "�����Ҿ�����ҵ"
214   "���ϼҾ�����ҵ"
219   "�����Ҿ�����ҵ"
221   "ֽ������ҵ"
222   "��ֽҵ"
223   "ֽ��Ʒҵ"
231   "ӡˢҵ"
232   "��¼ý��ĸ���"
241   "�Ļ���Ʒ����ҵ"
242   "������Ʒ����ҵ"
243   "����������������Ʒ����ҵ"
244   "�������ҵ"
245   "������������ҵ"
249   "������δ�������Ľ�������Ʒ����ҵ"
251   "����ԭ������ҵ"
252   "ԭ�ͼӹ�ҵ"
253   "ʯ����Ʒҵ"
257   "����ҵ"
261   "������ѧԭ������ҵ"
262   "��ѧ��������ҵ"
263   "��ѧũҩ����ҵ"
265   "�л���ѧ��Ʒ����ҵ"
266   "�ϳɲ�������ҵ"
267   "ר�û�ѧ��Ʒ����ҵ"
268   "���û�ѧ��Ʒ����ҵ"
271   "��ѧҩƷԭҩ����ҵ"
272   "��ѧҩƷ�Ƽ�����ҵ"
273   "��ҩ�ļ��г�ҩ�ӹ�ҵ"
274   "����ҩƷ����ҵ"
275   "������Ʒҵ"
281   "��ά����ά����ҵ"
282   "�ϳ���ά����ҵ"
285   "��߼���߲�������ҵ"
291   "��̥����ҵ"
292   "����̥����ҵ"
293   "�𽺰塢�ܡ�������ҵ"
294   "�������Ʒҵ"
295   "����������ҵ"
296   "��ѥЬ����ҵ"
297   "��������Ʒҵ"
298   "����Ʒ����ҵ"
299   "��������Ʒҵ"
301   "���ϱ�Ĥ����ҵ"
302   "���ϰ塢�ܡ���������ҵ"
303   "����˿��������֯Ʒ����ҵ"
304   "��ĭ���ϼ������ϳɸ�����ҵ"
305   "���ϰ�װ�估��������ҵ"
306   "����Ь����ҵ"
307   "����������Ʒ����ҵ"
308   "�����������ҵ"
309   "����������Ʒҵ"
311   "ˮ������ҵ"
312   "ˮ����Ʒ��ʯ��ˮ����Ʒҵ"
313   "ש�ߡ�ʯ�Һ����ʽ�����������ҵ"
314   "������������Ʒҵ"
315   "�մ���Ʒҵ"
316   "�ͻ������Ʒҵ"
317   "ʯī��̼����Ʒҵ"
318   "������ά������Ʒҵ"
319   "������δ�����ķǽ���������Ʒҵ"
321   "����ҵ"
322   "����ҵ"
324   "��ѹ�Ӽӹ�ҵ"
326   "���Ͻ�ұ��ҵ"
331   "����ɫ����ұ��ҵ"
332   "����ɫ����ұ��ҵ"
333   "�����ұ��ҵ"
334   "ϡ��ϡ������ұ��ҵ"
336   "��ɫ�����Ͻ�ҵ"
338   "��ɫ����ѹ�Ӽӹ�ҵ"
341   "�����ṹ����ҵ"
342   "����������ҵ"
343   "��������ҵ"
344   "��װ��ͽ�����װ��Ʒ����ҵ"
345   "����˿��������Ʒҵ"
346   "�����ý�����Ʒҵ"
347   "�������洦���ȴ���ҵ"
348   "���ý�����Ʒҵ"
349   "����������Ʒҵ"
351   "��¯��ԭ��������ҵ"
352   "�����ӹ���е����ҵ"
353   "ͨ���豸����ҵ"
354   "��С���������ҵ"
356   "����ͨ���㲿������ҵ"
357   "���ͼ�����ҵ"
358   "��ͨ��е����ҵ"
359   "������ͨ��е����ҵ"
361   "ұ�𡢿�ɽ�����繤ҵר���豸����ҵ"
362   "ʯ����������ҵר���豸����ҵ"
363   "��Ĺ�ҵר���豸����ҵ"
364   "ũ���֡������桢ˮ��ҵ��е����ҵ"
365   "ҽ����е����ҵ"
367   "����ר���豸����ҵ"
368   "ר�û�е�豸����ҵ"
371   "��·�����豸����ҵ"
372   "��������ҵ"
373   "Ħ�г�����ҵ"
374   "���г�����ҵ"
375   "�糵����ҵ"
376   "��������ҵ"
377   "���պ���������ҵ"
378   "��ͨ�����豸����ҵ"
379   "������ͨ�����豸����ҵ"
390   "������ҩ����ҵ"
401   "�������ҵ"
402   "����缰�����豸����ҵ"
404   "�繤��������ҵ"
406   "���õ�������ҵ"
407   "������������ҵ"
408   "������е����ҵ"
409   "����������е����ҵ"
411   "ͨ���豸����ҵ"
412   "�״�����ҵ"
413   "�㲥�����豸����ҵ"
414   "���Ӽ��������ҵ"
415   "������������ҵ"
416   "����Ԫ������ҵ"
417   "���õ�����������ҵ"
418   "�����豸��ͨ���豸����ҵ"
419   "���������豸����ҵ"
421   "ͨ�������Ǳ�����ҵ"
422   "ר�������Ǳ�����ҵ"
423   "���Ӳ�����������ҵ"
424   "������������ҵ"
425   "�Ļ����칫�û�е����ҵ"
426   "�ӱ�����ҵ"
428   "�����Ǳ��Ļ����칫�û�е����ҵ"
429   "���������Ǳ�����ҵ"
431   "��������Ʒ����ҵ"
435   "������Ʒ����ҵ"
439   "����������������Ʒ����ҵ"
441   "��������ҵ"
442   "������Ӧҵ"
443   "��������ˮ�����͹�Ӧҵ"
451   "ú������ҵ"
452   "ú����Ӧҵ"
461   "����ˮ����ҵ"
462   "����ˮ��Ӧҵ"
471   "���ݽ���ҵ"
472   "��ɽ����ҵ"
473   "��·����·���������������ҵ"
474   "�̰ӡ���վ����ͷ����ҵ"
479   "������ľ���̽���ҵ"
481   "��·���ܵ���װҵ"
482   "�豸��װҵ"
490   "װ��װ��ҵ"
501   "������ʿ���ҵ"
502   "������ʿ���ҵ"
503   "������ʿ�̽ҵ"
504   "���̵��ʿ���ҵ"
505   "�������ʿ���ҵ"
506   "��������͵���ѧ����ҵ"
509   "���ʹ��̼�����������������ҵ"
510   "ˮ������ҵ"
520   "��·����ҵ"
531   "��������ҵ"
539   "������·����ҵ"
540   "�ܵ�����ҵ"
551   "Զ������ҵ"
552   "�غ�����ҵ"
553   "�ںӡ��ں�����ҵ"
559   "����ˮ������ҵ"
561   "���տͻ�����ҵ"
562   "ͨ�ú���ҵ"
571   "��·��������ҵ"
572   "�ۿ�ҵ"
573   "ˮ�˸���ҵ"
574   "�������������丨��ҵ"
575   "װж����ҵ"
579   "������δ�����Ľ�ͨ���丨��ҵ"
580   "������ͨ����ҵ"
590   "�ִ�ҵ"
601   "����ҵ"
602   "����ҵ"
603   "�ʵ�ҵ"
611   "ʳƷ�����ϡ��̲�����ҵ"
612   "�ޡ��顢�����Ʒ����ҵ"
613   "��֯Ʒ����װ��Ьñ����ҵ"
614   "���ðٻ�����ҵ"
615   "������Ʒ����ҵ"
616   "��𡢽��硢��������ҵ"
617   "ҩƷ��ҽ����е����ҵ"
621   "��Դ����ҵ"
622   "������������ҵ"
623   "ľ������ҵ"
624   "������������ҵ"
625   "���Ʒ����ҵ"
626   "������������ҵ"
627   "��е�������豸����ҵ"
628   "������Ħ�г������������ҵ"
629   "�������ʻ�������ҵ"
631   "��������Ʒ����ҵ"
632   "ͼ�鱨������ҵ"
633   "ũҵ������������ҵ"
639   "������δ����������ҵ"
641   "ʳƷ�����Ϻ��̲�����ҵ"
642   "���ðٻ�����ҵ"
643   "��֯Ʒ����װ��Ьñ����ҵ"
644   "������Ʒ����ҵ"
645   "��𡢽��硢��������ҵ"
647   "ҩƷ��ҽ����е����ҵ"
648   "ͼ�鱨������ҵ"
649   "��������ҵ"
650   "��ҵ���������ҵ"
671   "����"
672   "���"
679   "��������ҵ"
681   "��������"
682   "��ҵ����"
683   "��������"
684   "���ú�����"
685   "����Ͷ��ҵ"
686   "֤ȯ�����뽻��ҵ"
687   "���������н���ҵ"
700   "����ҵ"
720   "���ز������뾭Ӫҵ"
730   "���ز�����ҵ"
740   "���ز������뾭��ҵ"
751   "���ڹ�����ͨҵ"
752   "԰���̻�ҵ"
753   "��Ȼ����������ҵ"
754   "��������ҵ"
755   "�������̹���ҵ"
756   "�羰��ʤ������ҵ"
759   "������������ҵ"
761   "�������ݻ�ױҵ"
762   "��ԡҵ"
763   "ϴȾҵ"
764   "��Ӱ����ӡҵ"
765   "�ж���"
766   "����Ʒ����ҵ"
767   "�������ҵ"
768   "����ҵ"
769   "�����������ҵ"
780   "�ù�ҵ"
790   "���޷���ҵ"
800   "����ҵ"
810   "���ַ���ҵ"
821   "���ҵ"
822   "��ѯ����ҵ"
829   "������δ��������Ϣ��ѯ����ҵ"
831   "���������ѯҵ"
832   "���ݴ���ҵ"
833   "���ݿ����ҵ"
834   "������豸ά����ѯҵ"
841   "�г��������ҵ"
849   "������δ������������ҵ"
851   "ҽԺ"
852   "����Ժ"
853   "ר�Ʒ�������վ��"
854   "��������վ"
855   "���ױ�����(վ)"
856   "ҩƷ������(��)"
859   "��������"
860   "����"
871   "��ḣ��ҵ"
872   "��ᱣ�պ;ȼ�ҵ"
879   "������δ��������ḣ������ҵ"
891   "�ߵȽ���"
892   "�еȽ���"
893   "���Ƚ���"
894   "ѧǰ����"
895   "�������"
899   "��������"
901   "����"
902   "����"
903   "���ﱣ��"
904   "ͼ���"
905   "������"
906   "Ⱥ���Ļ�"  
907   "����"  
908   "�Ļ��������������ҵ"  
909   "�����Ļ�����ҵ"  
911   "�㲥"  
912   "��Ӱ"
913   "����"   
921   "��Ȼ��ѧ�о�"  
922   "����ѧ�о�"   
923   "������ѧ�о�"   
931   "����"   
932   "����"
933   "���"   
934   "�����ල"  
935   "���󻷾�"  
936   "��������"  
937   "�����ƹ�ͿƼ���������ҵ"
938   "�������ҵ"
939   "�����ۺϼ�������ҵ"
940   "���һ���"
950   "��������"
960   "�������"
971   "����ίԱ��"
972   "����ίԱ��"
991   "��ҵ�������"
999   "������δ��������ҵ";

lab values industry industry;

ren r20 occup;

lab def occup
0  "no job"
10 "�й�����������ίԱ��͵ط���������֯������"
11 "�й�����������ίԱ��͵ط���������֯������"
20 "���һ��ؼ��乤������������"
21 "����Ȩ�����ؼ��乤������������"
22 "������Э���乤������������"
23 "����Ժ������"
24 "������Ժ������"
25 "�����������ؼ��乤������������"
29 "�������һ��ؼ��乤������������"
30 "�������ɺ�������弰�乤������������"
31 "�������ɸ�����"
32 "���ᡢ�����š��������������弰�乤������������"
33 "Ⱥ��������֯������"
39 "����������弰�乤������������"
40 "��ҵ��λ������"
41 "������ѧ��λ������"
42 "������λ������"
43 "���е�λ������"
49 "������ҵ��λ������"
50 "��ҵ������"
51 "��ҵ������"
110 "��ѧ�о���Ա"
111 "��ѧ�о���Ա"
112 "����ѧ�о���Ա"
113 "��ѧ�о���Ա"
114 "���ѧ�о���Ա"
115 "������ѧ�о���Ա"
116 "��ѧ�������о���Ա"
117 "ͼ���ѧ���鱨ѧ�о���Ա"
118 "��ʷѧ�о���Ա"
119 "�����ѧ�о���Ա"
121 "��ѧ�о���Ա"
122 "����ѧ�о���Ա"
123 "��ѧ�о���Ա"
124 "����ѧ�о���Ա"
125 "�����ѧ�о���Ա"
126 "�����ѧ�о���Ա"
127 "ũҵ��ѧ�о���Ա"
128 "ҽѧ�о���Ա"
129 "������ѧ�о���Ա"
130 "���̼�����Ա"
131 "���ʿ�̽���̼�����Ա"
132 "��湤�̼�����Ա"
133 "��ɽ���̼�����Ա"
134 "ʯ�͹��̼�����Ա"
135 "ұ�𹤳̼�����Ա"
136 "�������̼�����Ա"
137 "��е���̼�����Ա"
138 "�������̼�����Ա��"
139 "���չ��̼�����Ա"
141 "���칤�̼�����Ա"
142 "���ӹ��̼�����Ա"
143 "ͨ�Ź��̼�����Ա"
144 "�������Ӧ�ù��̼�����Ա"
145 "�������̼�����Ա"
146 "�������̼�����Ա"
147 "�������̼�����Ա"
148 "�㲥��Ӱ���ӹ��̼�����Ա"
149 "��ͨ���̼�����Ա"
151 "���ú��չ��̼�����Ա"
152 "��·���̼�����Ա"
153 "�������̼�����Ա"
154 "���Ĺ��̼�����Ա"
155 "��ҵ���̼�����Ա"
156 "ˮ�����̼�����Ա"
157 "���󹤼�����Ա"
158 "ˮ�����̼�����Ա"
159 "��֯���̼�����Ա"
161 "ʳƷ���̼�����Ա"
162 "���󹤳̼�����Ա"
163 "���𹤳̼�����Ա"
164 "�����������̼�����Ա"
165 "��ȫ���̼�����Ա"
166 "��׼�����������������̼�����Ա"
167 "������ҵ�����̼�����Ա"
169 "�������̼�����Ա"
170 "ũҵ������Ա"
171 "�������ϼ�����Ա"
172 "ֲ�ﱣ��������Ա"
173 "԰�ռ�����Ա"
174 "�����Ŵ��������༼����Ա"
175 "��ҽ����ҩ������Ա"
176 "�����Ͳ�ҵ������Ա"
179 "����ũҵ������Ա"
180 "�ɻ��ʹ���������Ա"
181 "������Ա���캽��Ա"
182 "����ָ�Ӻ�������Ա"
189 "�����ɻ��ʹ���������Ա"
190 "����רҵ������Ա"
191 "��ҽҽʦ"
192 "��ҽҽʦ"
193 "����ҽ���ҽʦ"
194 "����ҽʦ"
195 "��������ҽʦ"
196 "ҩ����Ա"
197 "ҽ�Ƽ�����Ա"
198 "������Ա"
199 "��������רҵ������Ա"
210 "����ҵ����Ա"
211 "���üƻ���Ա"
212 "ͳ����Ա"
213 "�����Ա"
214 "�����Ա"
215 "����������Ա"
219 "��������ҵ����Ա"
220 "����ҵ����Ա"
221 "����ҵ����Ա"
222 "����ҵ����Ա"
223 "֤ȯҵ����Ա"
229 "��������ҵ����Ա"
230 "����רҵ��Ա"
231 "����"
232 "����"
233 "��ʦ"
234 "��֤Ա"
235 "˾��������Ա"
236 "���Ա"
239 "��������רҵ��Ա"
240 "��ѧ��Ա"
241 "�ߵȽ�����ʦ"
242 "�е�ְҵ������ʦ"
243 "��ѧ��ʦ"
244 "Сѧ��ʦ"
245 "�׶���ʦ"
246 "���������ʦ"
249 "������ѧ��Ա"
250 "��ѧ����������Ա"
251 "���մ�����������Ա"
252 "�ർ������ָ����Ա"
253 "��Ա"
254 "��������Ա"
255 "��Ӱ������������̨רҵ��Ա"
256 "����רҵ��Ա"
257 "��������רҵ��Ա"
259 "������ѧ����������Ա"
260 "����������Ա"
261 "����������Ա"
270 "���ų��桢�Ļ�������Ա"
271 "����"
272 "�༭"
273 "У��Ա"
274 "����Ա����Ŀ������"
275 "����"
276 "ͼ�������뵵��ҵ����Ա"
277 "���ż����ﱣ��רҵ��Ա"
279 "�������ų���ʽ���Ļ�������Ա"
280 "�ڽ�ְҵ��"
280 "�ڽ�ְҵ��"
290 "����רҵ������Ա"
290 "����רҵ������Ա"
310 "�����칫��Ա"
311 "����ҵ����Ա"
312 "����������Ա"
319 "���������칫��Ա"
320 "��ȫ������������Ա"
321 "���񾯲�"
322 "�ΰ�������Ա"
323 "������Ա"
329 "������ȫ������������Ա"
330 "�����͵���ҵ����Ա"
331 "����ҵ����Ա"
332 "����ҵ����Ա"
333 "����ͨ�Ŵ���ҵ����Ա"
339 "���������͵���ҵ����Ա"
390 "����������Ա���й���Ա"
410 "������Ա"
411 "Ӫҵ��Ա"
412 "������չ����Ա"
413 "�ɹ���Ա"
414 "�������䵱������ҵ����Ա"
415 "�Ͼ����ʻ���������Ա"
416 "���͹�����Ա"
417 "��Ʒ�ල���г�������Ա"
419 "����������Ա"
420 "�ִ���Ա"
421 "������Ա"
422 "������Ա"
429 "�����ִ���Ա"
430 "����������Ա"
431 "�в������Ա"
432 "���������Ա"
433 "���ƺͲ�����Ա"
434 "Ӫ��������Ա"
435 "����������Ա"
439 "��������������Ա"
440 "���ꡢ���μ��������ֳ���������Ա"
441 "���������Ա"
442 "���μ�������������������Ա"
443 "��������ֳ���������Ա"
449 "�������ꡢ���μ��������ֳ���������Ա"
450 "�������Ա"
451 "��·��·���������Ա"
452 "��·�ͻ����������Ա"
453 "�������������Ա"
454 "ˮ�����������Ա"
459 "�������������Ա"
460 "ҽ����������������Ա"
461 "ҽ����������������Ա"
470 "������;������������Ա"
471 "����н������Ա"
472 "��ҵ������Ա"
473 "��ˮ�����ȼ�����ȼ��Ӧ������Ա"
474 "����������Ա"
475 "��Ӱ������Ա"
476 "����侵��Ա"
477 "ϴȾ֯����Ա"
478 "ԡ�ط�����Ա"
479 "ӡ�¿�����Ա"
481 "���û����Ʒά����Ա"
482 "�칫�豸ά����Ա"
483 "��������ͥ������Ա"
484 "����������Ա"
485 "���������Ա"
489 "����������;������������Ա"
490 "������ҵ������ҵ��Ա"
499 "������ҵ������ҵ��Ա"
510 "��ֲҵ������Ա"
511 "��������������Ա"
512 "ũҵʵ����Ա"
513 "԰������������Ա"
514 "�ȴ�����������Ա"
515 "��ҩ��������Ա"
516 "ũ�����ز�Ʒ�ӹ���Ա"
519 "������ֲҵ������Ա"
520 "��ҵ������Ұ����ֲ�ﱣ����Ա"
521 "Ӫ������Ա"
522 "ɭ����Դ�ܻ���Ա"
523 "Ұ����ֲ�ﱣ������Ȼ��������Ա"
524 "ľ�Ĳ�����Ա"
529 "������ҵ������Ұ����ֲ�ﱣ����Ա"
530 "����ҵ������Ա"
531 "����������Ա"
532 "����������Ա"
533 "�۷�������Ա"
534 "ʵ�鶯��������Ա"
535 "�����߲�������Ա"
536 "��ҵ������Ա"
539 "��������ҵ������Ա"
540 "��ҵ������Ա"
541 "ˮ����ֳ��Ա"
542 "ˮ�����̼��й���Ա"
543 "ˮ��Ʒ�ӹ���Ա"
549 "������ҵ������Ա"
550 "ˮ����ʩ����������Ա"
551 "�ӵ���ˮ�������Ա"
552 "ũ����Ź��̽������ά����Ա"
553 "ˮ��������ҵ��Ա"
554 "ˮ�Ŀ�����ҵ��Ա"
559 "����ˮ����ʩ����������Ա"
590 "����ũ���֡������桢ˮ��ҵ������Ա"
591 "ũ��ר�û�е������Ա"
592 "ũ����Դ����������Ա"
610 "���⼰���￪����Ա"
611 "���ʿ�����Ա"
612 "�����Ա"
613 "���￪����Ա"
614 "���ﴦ����Ա"
615 "�꾮��Ա"
616 "ʯ�͡���Ȼ��������Ա"
617 "��ҵ������Ա"
619 "�������⼰���￪����Ա"
620 "����ұ����������Ա"
621 "������Ա"
622 "���ֹ���"
623 "���Ͻ�ұ����Ա"
624 "����ɫ����ұ����Ա"
625 "����ɫ����ұ����Ա"
626 "ϡ�����ұ����Ա"
627 "�뵼������Ʊ���Ա"
628 "����������Ա"
629 "��������Ա"
631 "̼����Ʒ������Ա"
632 "Ӳ�ʺϽ�������Ա"
639 "��������ұ����������Ա"
640 "������Ʒ������Ա"
641 "������Ʒ����ͨ�ù�����Ա"
642 "ʯ������������Ա"
643 "ú����������Ա"
644 "��ѧ����������Ա"
645 "�޻�������Ʒ������Ա"
646 "�����л�������Ʒ������Ա"
647 "�ϳ���֬������Ա"
648 "�ϳ���������Ա"
649 "��ѧ��ά������Ա"
651 "�ϳɸ�������Ա"
652 "��ϸ������Ʒ������Ա"
653 "��Ϣ��¼����������Ա"
654 "��ҩ������Ա"
654 "ըҩ������Ա"
655 "�ֲ�������Ʒ������Ա"
656 "���ϲ��ϼӹ���Ա"
657 "���û�ѧƷ������Ա"
659 "����������Ʒ������Ա"
660 "��е����ӹ���Ա"
661 "��е��ӹ���Ա"
662 "��е�ȼӹ���Ա"
663 "���ּӹ��豸������Ա"
664 "�����ӽ�ӹ���Ա"
665 "�������洦��ӹ���Ա"
666 "ĥ��ĥ������ӹ���Ա"
667 "���������ӹ�������Ա"
669 "������������ӹ���Ա"
670 "�����Ʒװ����Ա"
671 "������������װ����Ա"
672 "��е�豸װ����Ա"
673 "�����豸װ����Ա"
674 "����Ԫ�����豸װ����Ա"
675 "����ר���豸װ�������Ա"
676 "�����Ǳ�װ����Ա"
677 "���䳵��װ����Ա"
678 "Ĥ��ˮ�����豸������Ա"
679 "ҽ����еװ�估��֫�������������Ա"
681 "���û�е��������װ����Ա"
682 "�����Ʒ������װ����Ա"
683 "װ�׳���װ����Ա"
684 "ǹ��������Ա"
685 "��������Ա"
686 "���żӹ�������Ա"
687 "��Ʒ������Ա"
688 "��������������Ա"
689 "����������Ա"
691 "���ղ�Ʒװ���������Ա"
692 "���ղ�Ʒ������Ա"
693 "��������װ�������Ա"
694 "���������װ��������Ա"
695 "�������ṹǿ�ȡ��¶ȡ�����������Ա"
696 "�г�������Ա"
699 "���������Ʒװ����Ա"
710 "��е�豸������Ա"
711 "��е�豸ά����Ա"
712 "�����Ǳ�������Ա"
713 "���ú�����ά����Ա"
719 "������е�豸������Ա"
720 "�����豸��װ�����С����޼�������Ա"
721 "�����豸��װ��Ա"
722 "��������ֵ����Ա"
723 "��硢��硢����豸ֵ����Ա"
724 "�����豸������Ա"
725 "���õ���Ա"
726 "������������豸��װ��������������Ա"
729 "���������豸��װ�����С����޼�������Ա"
730 "����Ԫ�������豸���졢װ�䡢���Լ�ά����Ա"
731 "��������������Ա"
732 "����Ԫ��������Ա"
733 "���������Ա"
734 "�����豸װ�䡢������Ա"
735 "���Ӳ�Ʒά����Ա"
739 "��������Ԫ�������豸���졢װ�䡢���Լ�ά����Ա"
740 "�𽺺�������Ʒ������Ա"
741 "����Ʒ������Ա"
742 "������Ʒ������Ա"
749 "�����𽺺�������Ʒ������Ա"
750 "��֯����֯��ӡȾ��Ա"
751 "��άԤ������Ա"
752 "��֯��Ա"
753 "֯����Ա"
754 "��֯��Ա"
755 "ӡȾ��Ա"
759 "������֯����֯��ӡȾ��Ա"
760 "�ü������Һ�Ƥ�ëƤ��Ʒ�ӹ�������Ա"
761 "�ü���������Ա"
762 "Ьñ������Ա"
763 "Ƥ�ëƤ�ӹ���Ա"
764 "������Ʒ�ټӹ���Ա"
769 "�����ü������Һ�Ƥ�ëƤ��Ʒ�ӹ�������Ա"
770 "���͡�ʳƷ�����������ӹ������������ӹ���Ա"
771 "���������ӹ���Ա"
772 "���Ǻ�����Ʒ�ӹ���Ա"
773 "��Ʒ����ʳƷ����ͷ������������Ա"
774 "��ơ�ʳƷ��Ӽ�����ζƷ������Ա"
775 "����ʳƷ������Ա"
776 "���׼ӹ���Ա"
777 "�⡢��ʳƷ�ӹ���Ա"
778 "���������ӹ���Ա"
779 "�������͡�ʳƷ�����������ӹ������������ӹ���Ա"
780 "�̲ݼ�����Ʒ�ӹ���Ա"
781 "ԭ�̸�����Ա"
782 "����������Ա"
783 "���ô�����ά˿���˰�������Ա"
789 "�����̲ݼ�����Ʒ�ӹ���Ա"
790 "ҩƷ������Ա"
791 "�ϳ�ҩ��������Ա"
792 "���＼����ҩ��Ʒ����Ա"
793 "ҩ���Ƽ���Ա"
794 "��ҩ��ҩ��Ա"
799 "����ҩƷ������Ա"
810 "ľ�ļӹ��������������ľ��Ʒ�������ƽ�����ֽ��ֽ��Ʒ�����ӹ���Ա"
811 "ľ�ļӹ���Ա"
812 "�����������Ա"
813 "ľ����Ʒ������Ա"
814 "�ƽ���Ա"
815 "��ֽ��Ա"
816 "��Ʒ������Ա"
819 "����ľ�ļӹ��������������ľ��Ʒ�������ƽ�����ֽ��ֽ��Ʒ�����ӹ���Ա"
820 "�������������ӹ���Ա"
821 "ˮ�༰ˮ����Ʒ�����ӹ���Ա"
822 "ǽ���������������Ա"
823 "������ˮ�ܷ����������Ա"
824 "�������¼���������������Ա"
825 "װ��ʯ��������Ա"
826 "�ǽ���������Ʒ�����ӹ���Ա"
827 "�ͻ����������Ա"
829 "�����������������ӹ���Ա"
830 "�������մɡ��´ɼ�����Ʒ�����ӹ���Ա"
831 "����������Ա"
832 "������ά����Ʒ������Ա"
833 "ʯӢ������Ʒ�ӹ���Ա"
834 "�մ���Ʒ������Ա"
835 "�´���Ʒ������Ա"
839 "�����������մɡ��´ɼ�����Ʒ�����ӹ���Ա"
840 "�㲥Ӱ����Ʒ���������ż����ﱣ����ҵ��Ա"
841 "Ӱ������������Ա"
842 "������Ʒ����������Ա"
843 "�㲥Ӱ����̨�豸��װ���Լ����в���Ա"
844 "��Ӱ��ӳ��Ա"
845 "���ﱣ����ҵ��Ա"
849 "�����㲥Ӱ����Ʒ���������ż����ﱣ����ҵ��Ա"
850 "ӡˢ��Ա"
851 "ӡǰ������Ա"
852 "ӡˢ������Ա"
853 "ӡ��������Ա"
859 "����ӡˢ��Ա"
860 "���ա�����Ʒ������Ա"
861 "�鱦���μӹ�������Ա"
862 "��̺������Ա"
863 "���������Ա"
864 "��������Ʒ������Ա"
865 "��ɴ���幤��Ʒ������Ա "
866 "��������Ʒ������Ա "
867 "��̹���Ʒ������Ա "
868 "����Ʒ������Ա"
869 "�������ա�����Ʒ������Ա"
870 "�Ļ�������������Ʒ������Ա"
871 "�Ľ���Ʒ������Ա"
872 "������Ʒ������Ա"
873 "����������Ա"
879 "�����Ļ�������������Ʒ������Ա"
880 "����ʩ����Ա"
881 "��ʯ��ʩ����Ա"
882 "������Ա"
883 "���������Ƽ���Ʒ�ӹ���Ա"
884 "�ֽ�ӹ���Ա"
885 "ʩ�����Ӵ�����Ա"
886 "���̷�ˮ��Ա"
887 "װ��װ����Ա"
888 "�Ž����޽���Ա"
889 "��·��������ά����Ա"
891 "�����豸��װ��Ա"
899 "��������ʩ����Ա"
910 "�����豸������Ա���й���Ա"
911 "��������·�����е�豸�������й���Ա"
912 "��·�����������е�豸�������й���Ա"
913 "���ú����豸�������й���Ա"
914 "ˮ�������豸�������й���Ա"
915 "����װж��е�������й���Ա"
919 "���������豸������Ա���й���Ա"
920 "�����������ﴦ����Ա"
921 "���������Ա"
922 "���󻷾�����������Ա"
923 "���ﴦ����Ա"
929 "���������������ﴦ����Ա"
930 "���顢������Ա"
931 "������Ա"
932 "���ղ�Ʒ������Ա"
933 "���������顢������Ա"
934 "������Ա"
939 "�������顢������Ա"
990 "���������������豸������Ա���й���Ա"
991 "��װ��Ա"
992 "���ò�����Ա"
993 "�������Ͷ���Ա"
999 "��������������ҵ��Ա";
#delimit cr
lab values occup occup

gen occup01=occup
recode occup01 1/99=1 100/299=2 300/390=3 400/499=4 500/599=5 600/993=6 999=9
lab def occup01 0 "no job"                              ///
                1 "���һ��ء���Ⱥ��֯����ҵ����ҵ��λ������"    ///
                2 "רҵ������Ա"                ///
                3 "������Ա���й���Ա"           ///
                4 "��ҵ������ҵ��Ա"             ///
                5 "ũ���֡������桢ˮ��ҵ������Ա"        ///
                6 "�����������豸������Ա���й���Ա"        ///
                9 "��������������ҵ��Ա"
lab values occup01 occup01

ren r211 ruemp
lab def ruemp 1 "��Уѧϰ" 2 "�������" 3 "������"  4 "ɥʧ�Ͷ�����" 5 "��δ���������ҹ���" 6 "ʧȥ���������ҹ���" 
lab values ruemp ruemp
lab var ruemp "R211, δ������״��"
tab ruemp

sum r212
ren r212 foccup
lab var foccup "R212, last occupation"
lab values foccup occup

tab r22
lab def r22  1 "���ݽ�" 2 "��ȡ���������" 3  "��ͥ������Ա����" 4 "�Ʋ�������" 5 "����" 6 "����"
lab values r22 r22
ren r22 uempinc
lab var uempinc "R22, not working: living source"

* marriage
tab r23
ren r23 marstat
lab def marstat 1 "δ��" 2 "��������ż" 3 "�ٻ�����ż" 4 "���" 5 "ɥż"
lab values marstat marstat
lab var marstat "1s 2m 3re-m 4d 5w"

ren r241 mary
ren r242 marm
lab var mary "yr 1st time married"
lab var marm "month 1st time married"

* fertility
sum r251 r252
tab1 r251 r252
gen byte r2501=int(r251/10)
gen byte r2502=r251-r2501*10

gen byte r253=int(r252/10)
gen byte r254=r252-r253*10

drop r251 r252
ren r2501 r251
ren r2502 r252
tab1 r251 r252 r253 r254

replace r251=r253 if r251==. & r253<.
replace r252=r254 if r252==. & r254<.

lab var r251 "(15-50�긾ŮF)fertility: # male"
lab var r252 "(15-50�긾Ů)fertility: # female"
lab var r253 "(15-50�긾Ů)fertility: # male alive now"
lab var r254 "(15-50�긾Ů)fertility: # female alive now"

* birth in last year
lab var r261 "(15��50�긾Ů)����״��, past yr"
lab def r261 1 "δ����" 2 "������"
lab value r261 r261
tab1 r261 r265
tab r261 if r251==.

lab var r262 "(15��50�긾Ů)��������״��, ����ʱ��_��"
lab var r263 "(15��50�긾Ů)��������״��, Ӥ���Ա�"
lab var r264 "(15��50�긾Ů)���������ĵڶ������ӵ�״��, ����ʱ��_��"
lab var r265 "(15��50�긾Ů)���������ĵڶ������ӵ�״��, Ӥ���Ա�"

* urban/rural
lab var rururban "urban or rural area"
lab def rururban 1 "urban" 2 "town" 3 "rural"
lab values rururban rururban
tab rururban

* province variables: 81, 82, 91?
lab def prov 11 Beijing 12 Tianjin 13 Hebei 14 Shanxi 15 InnerM 21 Liaoning 22 Jilin 23 Heilongjiang  ///
       31 Shanghai 32 Jiangsu 33 Zhejiang 34 Anhui 35 Fujian 36 Jiangxi 37 Shandong   ///
       41 Henan 42 Hubei 43 Hunan 44 Guangdong 45 Guangxi 46 Hainan 50 Chongqing 51 Sichuan 52 Guizhou 53 Yunan  54 Tibet ///
       61 Shaanxi 62 Gansu 63 Qinghai 64 Ningxia 65 Xinjiang 71 Taiwan 81 HongKong 82 Macau 91 abroad
qui for varlist province r63 r82 r102 r132: label value X prov
tab1 province r63 r82 r102 r132

list hhid htype h31 h32 h9 h10 h11 r7 if h9<5 & h10>900 & h10<. & hhid!=hhid[_n-1]

***********************************************************************************
* outfile the result, check in Excel, and read in checked data (done by cens00_chk01.dta)
* merge in checked variables (birth date, relationship, age...)
sort hhid fid
merge hhid fid using cens00_chk01
tab _merge
sum pid if birthy!=nbirthy & nbirthy<. | mary!=nmary & nmary!=.
replace birthy=nbirthy if nbirthy<.
replace mary=nmary if nmary<.
drop _merge nbirthy nmary

replace age=2000-birthy if birthy<.
replace age=age-1 if birthm>10 & birthm<.

* relationship is inconsistent with age (too old or too young)
sort hhid fid
merge hhid fid using cens00_chk03
tab _merge

replace birthy=birthy-age2+age if _merge==3 & age2<.
for varlist rel age male: replace X=X2 if X!=X2 & X2<. & _merge==3 
drop _merge rel2 age2 male2

* check variables
* # of HHs: change all one-person HH to "family HH"
sort hhid
sum hhid if hhid!=hhid[_n-1]
tab fsize htype if hhid!=hhid[_n-1]
replace htype=1 if fsize==1

* whole HH moved out=998, died=999, temporarily lived here for less than half year=997
* should not be sampled for long questionnaire (have already been deleted)
tab hhnumber if hhnumber>990 & hhnumber<1005

* check # of HH header: no problem (collective HH also has one head)
gen x1=1 if rel==0
sort hhid
egen x2=sum(x1), by(hhid)
tab x2

* collective HH: each person is a HH?
tab rel if htype==2
tab fsize if htype==2

* # of other member in each HH
sort hhid
gen x3=1 if rel==9
egen nnr=sum(x3), by(hhid)
tab nnr if hhid!=hhid[_n-1]

* # of rel<9
gen x5=fsize-nnr
tab x5 if hhid!=hhid[_n-1] & htype==2
tab x5 htype if hhid!=hhid[_n-1]

* how many others in HH?
tab fsize nnr if hhid!=hhid[_n-1] & fsize<10 & htype==1
tab fsize nnr if hhid!=hhid[_n-1] & fsize>=10 & fsize<40 & htype==1

list hhid rel male age educ feduc fsize if x5==1 & fsize>5 & htype==1, sepby(hhid)
list hhid rel male age educ feduc fsize if x5==2 & fsize>9 & htype==1, sepby(hhid)
drop x* nnr fsize

* school dorm.
*list hhid pid rel h31 h32 age educ feduc htype if hhid==13062264 | hhid==22070058 | hhid==22070147 |  ///
            hhid==22070152 | hhid==36040487
replace htype=2 if hhid==13062264 | hhid==22070058 | hhid==22070147 | hhid==22070152 | hhid==36040487
replace htype=2 if hhid==23020617 & fid>2

list hhid pid rel h31 h32 age educ feduc htype if hhid==51171056
replace htype=2 if hhid==51171056

* dorm?
list hhid fid rel age male marstat mary educ feduc emp r61 r9 r101 r12 if hhid==34120064 | hhid==43070007, sepby(hhid) nolab
replace age=18 if hhid==34120064 & fid==7
replace feduc=1 if hhid==34120064 & fid==7
replace age=14 if hhid==43070007 & (fid==9 | fid==10)
replace feduc=1 if hhid==43070007 & (fid==9 | fid==10)

* age & relationship: checked under EXCEL
* too old to be grandchild
* too old/young to be child
* parents
* grandparents

* family HH?
preserve
drop if htype==2
gen x1=1 if rel==9
sort hhid
egen nnr=sum(x1), by(hhid)
qui by hhid: gen np=_N
gen x4=nnr/np
sum x4 if hhid!=hhid[_n-1]
*tab fsize nnr if hhid!=hhid[_n-1] & x4>0.5 & x4<.
*list hhid pid rel male age educ feduc colive if np>5 & x4>0.6, sepby(hhid)

* emmigrants: too many?
sort hhid
sum hhid if hhid==hhid[_n-1] & h31!=h31[_n-1]
sum hhid if hhid==hhid[_n-1]& h51!=h51[_n-1]
*list hhid h31 h32 h51 h52 np nnr htype if hhid!=hhid[_n-1] & h51>5 & h51<. 
*list hhid h31 h32 h51 h52 np nnr htype if hhid!=hhid[_n-1] & h52>5 & h52<.
restore

list hhid h31 h32 h51 h52 htype if hhid!=hhid[_n-1] & h51>5 & h51<. & htype==2
list hhid h31 h32 h51 h52 htype if hhid!=hhid[_n-1] & h52>5 & h52<. & htype==2

replace h51=4 if hhid==15050040
replace h51=2 if hhid==31012673

replace h51=6 if hhid==36090384
replace h52=1 if hhid==36090384

replace h51=4 if hhid==41090201
replace h52=1 if hhid==41090201

replace h51=5 if hhid==41131012
replace h51=4 if hhid==43110347
replace h52=4 if hhid==43110347

replace h51=2 if hhid==44050483
replace h51=1 if hhid==46900656
replace h51=0 if hhid==51110451

replace h51=1 if hhid==43040875
replace h52=1 if hhid==43040875

replace h51=2 if hhid==52220186
replace h52=2 if hhid==52220186

replace h51=1 if hhid==62280391
replace h52=1 if hhid==62280391

replace h51=1 if hhid==37090557
replace h52=4 if hhid==37090557
replace h51=2 if hhid==44140303
replace h52=1 if hhid==44140303

replace h51=1 if hhid==42900221
replace h52=1 if hhid==42900221

replace h51=8 if hhid==51090801
replace h51=1 if hhid==54230001

replace h51=int(h51/10) if h51>9 & h52==0 & htype==1
replace h52=int(h52/10) if h52>9 & h51==0 & htype==1

sort hhid
list hhid h31 h32 h51 h52 htype if hhid!=hhid[_n-1] & h51>5 & h51<. 
list hhid h31 h32 h51 h52 htype if hhid!=hhid[_n-1] & h52>5 & h52<.

*********************************
* gender
sort hhid rel
gen male2=male[_n-1] if hhid==hhid[_n-1] & rel==1 & rel[_n-1]==0
tab male male2
drop male2

* age married
tab mary
tab marstat if mary==0
tab mary if age<15
replace mary=. if age<15 | marstat==1

gen mage=mary-birthy
tab mage
gen x7=1 if mage<10 | (mage>50 & mage<. & marstat!=3)
tab x7
sort hhid pid
egen x8=max(x7), by(hhid)
list hhid fid rel male educ birthy mary mage marstat emp uempinc if x8==1, sepby(hhid) nolab
drop mage x*

***************************
* birth, for 15-50 yrs old women
tab r251 if r252==.
tab r252 if r251==.

tab r251 male
tab r252 male
tab r251 if age<15 | age>50
list hhid fid male age rel if (age<15 | age>50) & r251>0 & r251<.
tab r252 if age<15 | age>50

replace r251=. if male==1 | age<15 | age>50
replace r252=. if male==1 | age<15 | age>50

* women between 15~50
gen x1=0
replace x1=1 if male==0 & age>14 & age<51

* missing chd
gen xm=1 if rel==2 & male==1
gen xf=1 if rel==2 & male==0
sort hhid
egen xmt=sum(xm), by(hhid)
egen xft=sum(xf), by(hhid)

* as HH header
tab xmt if r251==. & rel==0 & x1==1
tab xft if r252==. & rel==0 & x1==1

replace r251=xmt if rel==0 & x1==1 & r251==. & xmt>0
replace r252=xft if rel==0 & x1==1 & r252==. & xft>0

* as HH header's spouse
tab xmt if r251==. & rel==1 & x1==1
tab xmt if r251==. & rel==1 & x1==1 & marstat!=3

tab xft if r252==. & rel==1 & x1==1
tab xft if r252==. & rel==1 & x1==1 & marstat!=3

replace r251=xmt if rel==1 & x1==1 & r251==. & marstat!=3 & xmt>0
replace r252=xft if rel==1 & x1==1 & r252==. & marstat!=3 & xft>0
drop xm* xf*

* cross check with marital status
tab age marstat if x1==1 & r251==.
replace r251=0 if x1==1 & marstat==1 & r251==.
replace r252=0 if x1==1 & marstat==1 & r252==.

* marriage year
tab mary if x1==1 & r251==.
replace r251=0 if x1==1 & r251==. & (mary==1999 | mary==2000)
replace r252=0 if x1==1 & r252==. & (mary==1999 | mary==2000)

* fill in via marriage time
tab rel if x1==1 & r251==.
tab age if x1==1 & r251==.
replace r251=0 if x1==1 & r251==. & age<20
replace r252=0 if x1==1 & r252==. & age<20
drop x*

**********************************
* checked later
tab r61
tab r7

replace r61=1 if (hhid==12012400 & fid==2) | (hhid==12020499 & fid==3) | (hhid==14080039 & fid==2)
replace r7=1 if (hhid==12012400 & fid==2) | (hhid==12020499 & fid==3)
replace r7=2 if hhid==14080039 & fid==2

replace r61=2 if hhid==15020550
replace r62=7 if hhid==15020550
replace r7=1 if hhid==15020550

replace r61=1 if hhid==15030125 & fid==2
replace r7=2 if hhid==15030125 & fid==2

replace r61=2 if (hhid==41010151 | hhid==53011758) & fid==2
replace r62=7 if (hhid==41010151 | hhid==53011758) & fid==2

replace r61=1 if hhid==63210238 & fid==5

replace r7=1 if hhid==13090313 & (fid==3 | fid==4)
replace r7=1 if hhid==15210750 & (fid==1 | fid==2 | fid==3)
replace r7=1 if hhid==22070249 & fid==2
replace r61=1 if hhid==50010980 & fid==7
replace r7=2 if hhid==50010980 & fid==7

replace r61=1 if hhid==13040077 & fid==3
replace r7=1 if hhid==13090313 & fid==5

replace r102=. if hhid==34100309 & fid==3

replace r103=1 if hhid==31011509 & fid==2
replace r104=9 if hhid==31011509 & fid==2

replace r102=51 if (hhid==35050058 & fid==1) | (hhid==54010089 & fid==1)
replace r103=1 if hhid==14060089 & fid==4

replace r103=2 if hhid==35010563 & fid==2
replace r104=r103 if hhid==15270346 & fid==4
replace r103=2 if hhid==15270346 & fid==4
replace r103=1 if hhid==32120735 & fid==2

replace r103=3 if hhid==44200052 & fid==15
replace r103=1 if hhid==50020370 & fid==1
replace r103=2 if hhid==51040205 & fid==7
replace r103=1 if hhid==52010952 & fid==3
replace r103=3 if hhid==53260169 & fid==1
 
******************
replace r61=1 if r61==4 & (educ==8 | educ==9)
replace r7=2 if (r7==0 | r7==.) & (educ==8 | educ==9)

sort hhid rel fid
replace r61=r61[_n-1] if hhid==hhid[_n-1] & hhid==hhid[_n+1] & (rel[_n-1]==0 | rel[_n-1]==1) & rel==2 & rel[_n+1]==2 ///
       & r61==4 & r61[_n-1]==r61[_n+1]
replace r7=r7[_n-1] if hhid==hhid[_n-1] & hhid==hhid[_n+1] & (rel[_n-1]==0 | rel[_n-1]==1) & rel==2 & rel[_n+1]==2 ///
       & (r7==0 | r7==.) & r7[_n-1]==r7[_n+1]

replace r61=1 if r61==4 & r81==1 & r9==1
replace r61=1 if r61==4 & (occup01==1 | occup01==2)

replace r7=2 if (r7==0 | r7==.) & (occup01==1 | occup01==2)
replace r7=1 if (r7==0 | r7==.) & occup==511 & rururban==3

replace r61=r61[_n-1] if hhid==hhid[_n-1] & rel==2 & rel[_n-1]==2 & r61==4
replace r7=r7[_n-1] if hhid==hhid[_n-1] & rel==2 & rel[_n-1]==2 & (r7==0 | r7==.)
replace r61=r61[_n+1] if hhid==hhid[_n+1] & rel==2 & rel[_n+1]==2 & r61==4
replace r7=r7[_n+1] if hhid==hhid[_n+1] & rel==2 & rel[_n+1]==2 & (r7==0 | r7==.)

gen yr=2000-mary
sort hhid rel
replace r61=r61[_n-1] if hhid==hhid[_n-1] & rel[_n-1]==0 & rel==1 & r61==4 & yr>10 & yr<.
replace r61=r61[_n+1] if hhid==hhid[_n+1] & rel[_n+1]==1 & rel==0 & r61==4 & yr>10 & yr<.

tab r62 r61
replace r61=1 if (r61==2 | r61==3) & r62==0
replace r61=1 if r61==4 & (r12==2 | r12==3 | r12==6 | r12==7)
drop yr

**************************
sum hhid if (r101==1 | r101==2) & r102!=province & r102>10 & r102<.
sum hhid if r101==3 & r102==province & r102>10 & r102<.

sort r102 r103 r104
qui by r102 r103 r104: gen chk1=_N
replace chk1=. if r102==0 | r102==. | r103==0 | r103==. | r104==0 | r104==.
tab chk1 if chk1==1

sort hhid fid
replace r101=r101[_n-1] if hhid==hhid[_n-1] & chk1==1 & r63==r63[_n-1] & r102!=r102[_n-1] & r103==r103[_n-1] & ///
  r104==r104[_n-1] & chk1[_n-1]>1 & chk1[_n-1]<.
replace r101=r101[_n-1] if hhid==hhid[_n-1] & chk1==1 & r63==r63[_n-1] & r103!=r103[_n-1] & r102==r102[_n-1] & ///
  r104==r104[_n-1] & chk1[_n-1]>1 & chk1[_n-1]<.

replace r102=r102[_n-1] if hhid==hhid[_n-1] & chk1==1 & r63==r63[_n-1] & r102!=r102[_n-1] & r103==r103[_n-1] & ///
  r104==r104[_n-1] & chk1[_n-1]>1 & chk1[_n-1]<.
replace r103=r103[_n-1] if hhid==hhid[_n-1] & chk1==1 & r63==r63[_n-1] & r103!=r103[_n-1] & r102==r102[_n-1] & ///
  r104==r104[_n-1] & chk1[_n-1]>1 & chk1[_n-1]<.

drop chk1
sort r102 r103 r104
qui by r102 r103 r104: gen chk1=_N
replace chk1=. if r102==0 | r102==. | r103==0 | r103==. | r104==0 | r104==.
tab chk1 if chk1==1

sort hhid fid
replace r101=r101[_n+1] if hhid==hhid[_n+1] & chk1==1 & r63==r63[_n+1] & r102!=r102[_n+1] & r103==r103[_n+1] & ///
  r104==r104[_n+1] & chk1[_n+1]>1 & chk1[_n+1]<.
replace r101=r101[_n+1] if hhid==hhid[_n+1] & chk1==1 & r63==r63[_n+1] & r103!=r103[_n+1] & r102==r102[_n+1] & ///
  r104==r104[_n+1] & chk1[_n+1]>1 & chk1[_n+1]<.

replace r102=r102[_n+1] if hhid==hhid[_n+1] & chk1==1 & r63==r63[_n+1] & r102!=r102[_n+1] & r103==r103[_n+1] & ///
  r104==r104[_n+1] & chk1[_n+1]>1 & chk1[_n+1]<.
replace r103=r103[_n+1] if hhid==hhid[_n+1] & chk1==1 & r63==r63[_n+1] & r103!=r103[_n+1] & r102==r102[_n+1] & ///
  r104==r104[_n+1] & chk1[_n+1]>1 & chk1[_n+1]<.
drop chk1

replace r103=5 if hhid==14050381 & fid==4
replace r103=2 if hhid==21020872 & fid==2
replace r103=2 if hhid==21021605 & fid==4
replace r103=7 if hhid==22070148 & fid==1
replace r103=3 if hhid==23010900 & fid==1
replace r103=2 if hhid==42120139
replace r103=25 if hhid==43130044
replace r104=3 if hhid==43130044
replace r102=41 if hhid==44030967 & fid==8
replace r103=8 if hhid==44191036 & fid==1
replace r102=51 if hhid==44200179 & fid==4
replace r102=45 if hhid==45260287 & fid==1
replace r103=1 if hhid==52010193 & fid==2

sum hhid if (r101==1 | r101==2) & r102!=province & r102>10 & r102<.
sum hhid fid if r101==3 & r102==province & r102>10 & r102<.
replace r101=2 if r102==province & r102>10 & r102<.

sort hhid
qui by hhid: gen hsize=_N
lab var hsize "HH size, including collective HH"
tab hsize if hhid!=hhid[_n-1]

* household used systematic sampling
gen rwgt=1/hsize
lab var rwgt "re-weight by HH size"
sum rwgt

******************************************
drop yrssch
compress
order hhid fid pid hhnumber prov district
sort hhid pid
sum
d
save cens0001, replace
log close

