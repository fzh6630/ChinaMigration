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

lab var district "地级市代码"

lab def yesno 1 "是" 2 "否"

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
lab var h2 "户别"
lab def h2 1 "家庭户" 2 "集体户"
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
lab var h61 "# male 暂住本地，离开户籍地<0.5yr"
lab var h62 "# female 暂住本地，离开户籍地<0.5yr"

tab1 h71 h72
lab var h71 "# male: born in past one yr"
lab var h72 "# female: born in past one yr"

tab1 h81 h82
lab var h81 "# male: died in past one yr"
lab var h82 "# female: died in past one yr"

******************************
* housing

lab var h9 "住房间数"
lab var h10 "住房建筑面积, 平米"

lab var h11 "住房用途"
lab def h11 1 "生活用房" 2 "用作生产经营用房"
lab value h11 h11

* h12: share-housing or not (why 0? --unusual residential place?)
tab h12
ren h12 colive
tab colive htype
lab var colive "本住房中是否有其他合住户"

sum h13
recode h13 1=.
lab var h13 "本座住宅建筑时间，年"

lab var h14 "建筑层数"
lab def h14 1 "平房" 2 "六层及以下楼房" 3 "七层及以上楼房"
lab value h14 h14

lab var h15 "建筑外墙墙体材料"
lab def h15 1 "钢筋混凝土结构" 2 "砖石" 3 "砖木结构" 4 "木、竹、草" 5 "其他"
lab value h15 h15

lab var h16 "住房内有无厨房"
lab def h16 1 "本户独立使用" 2 "本户与其他户合用" 3 "无"
lab value h16 h16

lab var h17 "主要炊事燃料"
lab def h17 1 "燃气" 2 "电" 3 "煤炭" 4 "柴草" 5 "其他"
lab value h17 h17

lab var h18 "是否饮用自来水"
lab value h18 yesno

lab var h19 "住房内有无洗澡设施" 
lab def h19 1 "统计供热水" 2 "家庭自装热水器" 3 "其他" 4 "无"
lab value h19 h19

lab var h20 "住房内有无厕所"
lab def h20 1 "独立使用抽水式" 2 "邻居合用抽水式" 3 "独立使用其他式样" 4 "邻居合用其他式样" 5 "其他"
lab value h20 h20

lab var h21 "住房来源"
lab def h21 1 "自建住房" 2 "购买商品房" 3 "购买经济适用房" 4 "购买原公有住房" 5 "租赁公有住房" 6 "租赁商品住房" 7 "其他"
lab value h21 h21

lab var h22 "购建住房费用"
lab def h22 1 "<1万" 2 "1-2万"  3 "2-3万"  4 "3-5万"  5 "5-10万"  6 "10-20万"  7 "20-30万"  8 "30-50万"  9 ">50万"
lab values h22 h22

lab var h23 "月租房费用"
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
lab var ethnic "民族"
lab def eth 1 "汉族"  2 "蒙古族"  3 "回族"  4 "藏族"  5 "维吾尔族"  6 "苗族"  7 "彝族"  8 "壮族"  9 "布依族"  10 "朝鲜族" ///
            11 "满族" 12 "侗族"  13 "瑶族"  14 "白族" 15 "土家族" 16 "哈尼族" 17 "哈萨克族"       18 "傣族"  19 "黎族" ///
            20 "傈僳族"  21 "佤族"  22 "畲族"  23 "高山族"  24 "拉祜族"  25 "水族"  26 "东乡族"  27 "纳西族"  28 "景颇族"  ///
            29 "柯尔克孜族"  30 "土族"  31 "达斡尔族"  32 "仫佬族"  33 "羌族"  34 "布朗族"  35 "撒拉族"  36 "毛南族"  ///
            37 "仡佬族"     38 "锡伯族"  39 "阿昌族"  40 "普米族" 41 "塔吉克族" 42 "怒族" 43 "乌孜别克族" 44 "俄罗斯族" ///
            45 "鄂温克族"   46 "德昴族"  47 "保安族"  48 "裕固族" 49 "京族" 50 "塔塔尔族" 51 "独龙族"     52 "鄂伦春族"  ///
            53 "赫哲族"    54 "门巴族"   55 "珞巴族"  56 "基诺族"         97  "其它未识别民族"     98 "外国人入中国籍"
lab value ethnic eth
tab ethnic

* Hukou (R6)
* r62=0: skip the question (r61=1,4,5)
lab var r61 "户口登记状况：居住地与登记地"
lab def r61   1 "居住本乡/街，户口在本乡/街" ///
               2 "居住本乡/街>半年，户口外乡/街" ///
               3 "居住本乡/街<半年，离开户口登记地>半年" ///
               4 "居住本乡/街，户口待定"  ///
               5 "原住本乡/街，国外"
lab values r61 r61
tab r61

lab var r62 "户口登记状况：登记地"
lab def r62 1 "本县（市）其他乡"  2 "本县（市）其他镇"  3 "本县（市）其他街道"  4 "本市区其他乡"  5 "本市区其他镇"  ///
              6 "本市区其他街道"    7 "本省其他县（市）、市区"    8 "省外"
lab values r62 r62
tab r62
tab r62 r61

lab var r63 "户口登记状况：登记地，外省"

* r7=0: no hukou
tab r7
tab r61 r7
lab def r7  1 "农业户口" 2 "非农户口"
lab values r7 r7
lab var r7 "Hukou status: 0=no hukou 1=agr. 2=urban"

lab def r81 1 "本县、市、区"  2 "本省外县、市、区"  3 "外省"
lab values r81 r81
tab r81

tab r82
lab var r81 "where was born 1=cur. county 2=cur. prov 3=oth prov"
lab var r82 "where was born: (other)prov."

lab var r9 "which yr move here"
lab def r9 1 "出生后一直住本乡镇街道" 2 "1995.10.31以前" 3 "1995.11.1-12.31" 4 "1996年"  5 "1997年" 6 "1998年"  7 "1999年"  8 "2000年"
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
lab def r11	1 "乡" 2 "镇的居委会" 3 "镇的村委会" 4 "街道"
lab values r11 r11
tab r11
tab r11 r101
lab var r11 "R11: type of community moved out"

lab def r12 1 "务工经商" 2 "工作调动" 3 "分配录用" 4 "学习培训" 5 "拆迁搬家" 6 "婚姻迁入" 7 "随迁家属" 8 "投亲靠友" 9 "其他"
lab values r12 r12
tab r12
lab var r12 "R12 why move"

* 0: be here 5 yrs ago, or yonger than 5-yrs-old
lab def r131 1 "省内" 2 "省外"
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
lab def feduc 1 "在校" 2 "毕业" 3 "肄业" 4 "辍学" 5 "其他"
lab values feduc feduc
tab feduc
lab var feduc "finish schooling?"

ren r17 emp
lab def emp 1 "是" 2 "在职休假,学习,临时停工或季节性歇业未工作" 3 "未做任何工作"
lab values emp emp
tab emp
lab var emp "R17, 上周是否从事了一小时以上有收入的劳动"

ren r18 empday
tab empday
lab var empday "# dyas worked last wk"

sum r19 r20
ren r19 industry

#delimit ;
lab def industry
0    "no job"
11   "种植业"
19   "其他农业"
20   "林业"
31   "牲畜饲养放牧业"
32   "家禽饲养业"
33   "狩猎业"
39   "其他畜牧业"
41   "海洋渔业"
42   "淡水渔业"
51   "农业服务业"
52   "林业服务业"
53   "畜牧兽医服务业"
54   "渔业服务业"
59   "其他农、林、牧、渔服务业"
61   "煤炭开采业"
62   "煤炭洗选业"
71   "天然原油开采业"
72   "天然气开采业"
73   "油页岩开采业"
81   "铁矿采选业"
82   "其他黑色金属矿采选业"
91   "重有色金属矿采选业"
93   "轻有色金属矿采选业"
95   "贵金属矿采选业"
96   "稀有稀土金属矿采选业"
101   "土砂石开采业"
102   "化学矿采选业"
103   "采盐业"
109   "其他非金属矿采选业"
110   "其他矿采选业"
121   "木材采运业"
122   "竹材采运业"
131   "粮食及饲料加工业"
132   "植物油加工业"
133   "制糖业"
134   "屠宰及肉类蛋类加工业"
135   "水产品加工业"
136   "盐加工业"
139   "其他食品加工业"
141   "糕点、糖果制造业"
142   "乳制品制造业"
143   "罐头食品制造业"
144   "发酵制品业"
145   "调味品制造业"
149   "其他食品制造业"
151   "酒精及饮料酒制造业"
152   "软饮料制造业"
155   "制茶业"
159   "其他饮料制造业"
161   "烟叶复烤业"
162   "卷烟制造业"
169   "其他烟草加工业"
171   "纤维原料初步加工业"
172   "棉纺织业"
174   "毛纺织业"
176   "麻纺织业"
177   "丝绢纺织业"
178   "针织品业"
179   "其他纺织业"
181   "服装制造业"
182   "制帽业"
183   "制鞋业"
189   "其他纤维制品制造业"
191   "制革业"
192   "皮革制品制造业"
193   "毛皮鞣制及制品业"
195   "羽毛（绒）及制品业"
201   "锯材、木片加工业"
202   "人造板制造业"
203   "木制品业"
204   "竹、藤、棕、草制品业"
211   "木制家具制造业"
212   "竹、藤家具制造业"
213   "金属家具制造业"
214   "塑料家具制造业"
219   "其他家具制造业"
221   "纸浆制造业"
222   "造纸业"
223   "纸制品业"
231   "印刷业"
232   "记录媒介的复制"
241   "文化用品制造业"
242   "体育用品制造业"
243   "乐器及其他文娱用品制造业"
244   "玩具制造业"
245   "游艺器材制造业"
249   "其他类未包括的文教体育用品制造业"
251   "人造原油生产业"
252   "原油加工业"
253   "石油制品业"
257   "炼焦业"
261   "基本化学原料制造业"
262   "化学肥料制造业"
263   "化学农药制造业"
265   "有机化学产品制造业"
266   "合成材料制造业"
267   "专用化学产品制造业"
268   "日用化学产品制造业"
271   "化学药品原药制造业"
272   "化学药品制剂制造业"
273   "中药材及中成药加工业"
274   "动物药品制造业"
275   "生物制品业"
281   "纤维素纤维制造业"
282   "合成纤维制造业"
285   "渔具及渔具材料制造业"
291   "轮胎制造业"
292   "力车胎制造业"
293   "橡胶板、管、带制造业"
294   "橡胶零件制品业"
295   "再生橡胶制造业"
296   "橡胶靴鞋制造业"
297   "日用橡胶制品业"
298   "橡胶制品翻修业"
299   "其他橡胶制品业"
301   "塑料薄膜制造业"
302   "塑料板、管、棒材制造业"
303   "塑料丝、绳及编织品制造业"
304   "泡沫塑料及人造革、合成革制造业"
305   "塑料包装箱及容器制造业"
306   "塑料鞋制造业"
307   "日用塑料杂品制造业"
308   "塑料零件制造业"
309   "其他塑料制品业"
311   "水泥制造业"
312   "水泥制品和石棉水泥制品业"
313   "砖瓦、石灰和轻质建筑材料制造业"
314   "玻璃及玻璃制品业"
315   "陶瓷制品业"
316   "耐火材料制品业"
317   "石墨及碳素制品业"
318   "矿物纤维及其制品业"
319   "其他类未包括的非金属矿物制品业"
321   "炼铁业"
322   "炼钢业"
324   "钢压延加工业"
326   "铁合金冶炼业"
331   "重有色金属冶炼业"
332   "轻有色金属冶炼业"
333   "贵金属冶炼业"
334   "稀有稀土金属冶炼业"
336   "有色金属合金业"
338   "有色金属压延加工业"
341   "金属结构制造业"
342   "铸铁管制造业"
343   "工具制造业"
344   "集装箱和金属包装物品制造业"
345   "金属丝绳及其制品业"
346   "建筑用金属制品业"
347   "金属表面处理及热处理业"
348   "日用金属制品业"
349   "其他金属制品业"
351   "锅炉及原动机制造业"
352   "金属加工机械制造业"
353   "通用设备制造业"
354   "轴承、阀门制造业"
356   "其他通用零部件制造业"
357   "铸锻件制造业"
358   "普通机械修理业"
359   "其他普通机械制造业"
361   "冶金、矿山、机电工业专用设备制造业"
362   "石化及其他工业专用设备制造业"
363   "轻纺工业专用设备制造业"
364   "农、林、牧、渔、水利业机械制造业"
365   "医疗器械制造业"
367   "其他专用设备制造业"
368   "专用机械设备修理业"
371   "铁路运输设备制造业"
372   "汽车制造业"
373   "摩托车制造业"
374   "自行车制造业"
375   "电车制造业"
376   "船舶制造业"
377   "航空航天器制造业"
378   "交通运输设备修理业"
379   "其他交通运输设备制造业"
390   "武器弹药制造业"
401   "电机制造业"
402   "输配电及控制设备制造业"
404   "电工器材制造业"
406   "日用电器制造业"
407   "照明器具制造业"
408   "电气机械修理业"
409   "其他电气机械制造业"
411   "通信设备制造业"
412   "雷达制造业"
413   "广播电视设备制造业"
414   "电子计算机制造业"
415   "电子器件制造业"
416   "电子元件制造业"
417   "日用电子器具制造业"
418   "电子设备及通信设备修理业"
419   "其他电子设备制造业"
421   "通用仪器仪表制造业"
422   "专用仪器仪表制造业"
423   "电子测量仪器制造业"
424   "计量器具制造业"
425   "文化、办公用机械制造业"
426   "钟表制造业"
428   "仪器仪表及文化、办公用机械修理业"
429   "其他仪器仪表制造业"
431   "工艺美术品制造业"
435   "日用杂品制造业"
439   "其他生产、生活用品制造业"
441   "电力生产业"
442   "电力供应业"
443   "蒸汽、热水生产和供应业"
451   "煤气生产业"
452   "煤气供应业"
461   "自来水生产业"
462   "自来水供应业"
471   "房屋建筑业"
472   "矿山建筑业"
473   "铁路、公路、遂道、桥梁建筑业"
474   "堤坝、电站、码头建筑业"
479   "其他土木工程建筑业"
481   "线路、管道安装业"
482   "设备安装业"
490   "装修装饰业"
501   "区域地质勘查业"
502   "海洋地质勘查业"
503   "矿产地质勘探业"
504   "工程地质勘查业"
505   "环境地质勘查业"
506   "地球物理和地球化学勘查业"
509   "地质工程技术及其他技术服务业"
510   "水利管理业"
520   "铁路运输业"
531   "汽车运输业"
539   "其他公路运输业"
540   "管道运输业"
551   "远洋运输业"
552   "沿海运输业"
553   "内河、内湖运输业"
559   "其他水上运输业"
561   "航空客货运输业"
562   "通用航空业"
571   "公路管理及养护业"
572   "港口业"
573   "水运辅助业"
574   "机场及航空运输辅助业"
575   "装卸搬运业"
579   "其他类未包括的交通运输辅助业"
580   "其他交通运输业"
590   "仓储业"
601   "邮政业"
602   "电信业"
603   "邮电业"
611   "食品、饮料、烟草批发业"
612   "棉、麻、土畜产品批发业"
613   "纺织品、服装和鞋帽批发业"
614   "日用百货批发业"
615   "日用杂品批发业"
616   "五金、交电、化工批发业"
617   "药品及医疗器械批发业"
621   "能源批发业"
622   "化工材料批发业"
623   "木材批发业"
624   "建筑材料批发业"
625   "矿产品批发业"
626   "金属材料批发业"
627   "机械、电子设备批发业"
628   "汽车、摩托车及零配件批发业"
629   "再生物资回收批发业"
631   "工艺美术品批发业"
632   "图书报刊批发业"
633   "农业生产资料批发业"
639   "其他类未包括的批发业"
641   "食品、饮料和烟草零售业"
642   "日用百货零售业"
643   "纺织品、服装和鞋帽零售业"
644   "日用杂品零售业"
645   "五金、交电、化工零售业"
647   "药品及医疗器械零售业"
648   "图书报刊零售业"
649   "其他零售业"
650   "商业经纪与代理业"
671   "正餐"
672   "快餐"
679   "其他餐饮业"
681   "中央银行"
682   "商业银行"
683   "其他银行"
684   "信用合作社"
685   "信托投资业"
686   "证券经纪与交易业"
687   "其他非银行金融业"
700   "保险业"
720   "房地产开发与经营业"
730   "房地产管理业"
740   "房地产代理与经纪业"
751   "市内公共交通业"
752   "园林绿化业"
753   "自然保护区管理业"
754   "环境卫生业"
755   "市政工程管理业"
756   "风景名胜区管理业"
759   "其他公共服务业"
761   "理发及美容化妆业"
762   "沐浴业"
763   "洗染业"
764   "摄影及扩印业"
765   "托儿所"
766   "日用品修理业"
767   "家务服务业"
768   "殡葬业"
769   "其他居民服务业"
780   "旅馆业"
790   "租赁服务业"
800   "旅游业"
810   "娱乐服务业"
821   "广告业"
822   "咨询服务业"
829   "其他类未包括的信息咨询服务业"
831   "软件开发咨询业"
832   "数据处理业"
833   "数据库服务业"
834   "计算机设备维护咨询业"
841   "市场管理服务业"
849   "其他类未包括的社会服务业"
851   "医院"
852   "疗养院"
853   "专科防治所（站）"
854   "卫生防疫站"
855   "妇幼保健所(站)"
856   "药品检验所(室)"
859   "其他卫生"
860   "体育"
871   "社会福利业"
872   "社会保险和救济业"
879   "其他类未包括的社会福利保障业"
891   "高等教育"
892   "中等教育"
893   "初等教育"
894   "学前教育"
895   "特殊教育"
899   "其他教育"
901   "艺术"
902   "出版"
903   "文物保护"
904   "图书馆"
905   "档案馆"
906   "群众文化"  
907   "新闻"  
908   "文化艺术经纪与代理业"  
909   "其他文化艺术业"  
911   "广播"  
912   "电影"
913   "电视"   
921   "自然科学研究"  
922   "社会科学研究"   
923   "其他科学研究"   
931   "气象"   
932   "地震"
933   "测绘"   
934   "技术监督"  
935   "海洋环境"  
936   "环境保护"  
937   "技术推广和科技交流服务业"
938   "工程设计业"
939   "其他综合技术服务业"
940   "国家机关"
950   "政党机关"
960   "社会团体"
971   "居民委员会"
972   "村民委员会"
991   "企业管理机构"
999   "其他类未包括的行业";

lab values industry industry;

ren r20 occup;

lab def occup
0  "no job"
10 "中国共产党中央委员会和地方各级党组织负责人"
11 "中国共产党中央委员会和地方各级党组织负责人"
20 "国家机关及其工作机构负责人"
21 "国家权力机关及其工作机构负责人"
22 "人民政协及其工作机构负责人"
23 "人民法院负责人"
24 "人民检察院负责人"
25 "国家行政机关及其工作机构负责人"
29 "其他国家机关及其工作机构负责人"
30 "民主党派和社会团体及其工作机构负责人"
31 "民主党派负责人"
32 "工会、共青团、妇联等人民团体及其工作机构负责人"
33 "群众自治组织负责人"
39 "其他社会团体及其工作机构负责人"
40 "事业单位负责人"
41 "教育教学单位负责人"
42 "卫生单位负责人"
43 "科研单位负责人"
49 "其他事业单位负责人"
50 "企业负责人"
51 "企业负责人"
110 "科学研究人员"
111 "哲学研究人员"
112 "经济学研究人员"
113 "法学研究人员"
114 "社会学研究人员"
115 "教育科学研究人员"
116 "文学、艺术研究人员"
117 "图书馆学、情报学研究人员"
118 "历史学研究人员"
119 "管理科学研究人员"
121 "数学研究人员"
122 "物理学研究人员"
123 "化学研究人员"
124 "天文学研究人员"
125 "地球科学研究人员"
126 "生物科学研究人员"
127 "农业科学研究人员"
128 "医学研究人员"
129 "其他科学研究人员"
130 "工程技术人员"
131 "地质勘探工程技术人员"
132 "测绘工程技术人员"
133 "矿山工程技术人员"
134 "石油工程技术人员"
135 "冶金工程技术人员"
136 "化工工程技术人员"
137 "机械工程技术人员"
138 "兵器工程技术人员工"
139 "航空工程技术人员"
141 "航天工程技术人员"
142 "电子工程技术人员"
143 "通信工程技术人员"
144 "计算机与应用工程技术人员"
145 "电气工程技术人员"
146 "电力工程技术人员"
147 "邮政工程技术人员"
148 "广播电影电视工程技术人员"
149 "交通工程技术人员"
151 "民用航空工程技术人员"
152 "铁路工程技术人员"
153 "建筑工程技术人员"
154 "建材工程技术人员"
155 "林业工程技术人员"
156 "水利工程技术人员"
157 "海洋工技术人员"
158 "水产工程技术人员"
159 "纺织工程技术人员"
161 "食品工程技术人员"
162 "气象工程技术人员"
163 "地震工程技术人员"
164 "环境保护工程技术人员"
165 "安全工程技术人员"
166 "标准化、计量、质量工程技术人员"
167 "管理（工业）工程技术人员"
169 "其他工程技术人员"
170 "农业技术人员"
171 "土壤肥料技术人员"
172 "植物保护技术人员"
173 "园艺技术人员"
174 "作物遗传育种栽培技术人员"
175 "兽医、兽药技术人员"
176 "畜牧和草业技术人员"
179 "其他农业技术人员"
180 "飞机和船舶技术人员"
181 "飞行人员和领航人员"
182 "船舶指挥和引航人员"
189 "其他飞机和船舶技术人员"
190 "卫生专业技术人员"
191 "西医医师"
192 "中医医师"
193 "中西医结合医师"
194 "民族医师"
195 "公共卫生医师"
196 "药剂人员"
197 "医疗技术人员"
198 "护理人员"
199 "其他卫生专业技术人员"
210 "经济业务人员"
211 "经济计划人员"
212 "统计人员"
213 "会计人员"
214 "审计人员"
215 "国际商务人员"
219 "其他经济业务人员"
220 "金融业务人员"
221 "银行业务人员"
222 "保险业务人员"
223 "证券业务人员"
229 "其他金融业务人员"
230 "法律专业人员"
231 "法官"
232 "检察官"
233 "律师"
234 "公证员"
235 "司法鉴定人员"
236 "书记员"
239 "其他法律专业人员"
240 "教学人员"
241 "高等教育教师"
242 "中等职业教育教师"
243 "中学教师"
244 "小学教师"
245 "幼儿教师"
246 "特殊教育教师"
249 "其他教学人员"
250 "文学艺术工作人员"
251 "文艺创作和评论人员"
252 "编导和音乐指挥人员"
253 "演员"
254 "乐器演奏员"
255 "电影电视制作及舞台专业人员"
256 "美术专业人员"
257 "工艺美术专业人员"
259 "其他文学艺术工作人员"
260 "体育工作人员"
261 "体育工作人员"
270 "新闻出版、文化工作人员"
271 "记者"
272 "编辑"
273 "校对员"
274 "播音员及节目主持人"
275 "翻译"
276 "图书资料与档案业务人员"
277 "考古及文物保护专业人员"
279 "其他新闻出版式、文化工作人员"
280 "宗教职业者"
280 "宗教职业者"
290 "其他专业技术人员"
290 "其他专业技术人员"
310 "行政办公人员"
311 "行政业务人员"
312 "行政事务人员"
319 "其他行政办公人员"
320 "安全保卫和消防人员"
321 "人民警察"
322 "治安保卫人员"
323 "消防人员"
329 "其他安全保卫和消防人员"
330 "邮政和电信业务人员"
331 "邮政业务人员"
332 "电信业务人员"
333 "电信通信传输业务人员"
339 "其他邮政和电信业务人员"
390 "其他办事人员和有关人员"
410 "购销人员"
411 "营业人员"
412 "推销、展销人员"
413 "采购人员"
414 "拍卖、典当及租赁业务人员"
415 "废旧物资回收利用人员"
416 "粮油管理人员"
417 "商品监督和市场管理人员"
419 "其他购销人员"
420 "仓储人员"
421 "保管人员"
422 "储运人员"
429 "其他仓储人员"
430 "餐饮服务人员"
431 "中餐烹饪人员"
432 "西餐烹饪人员"
433 "调酒和茶艺人员"
434 "营养西餐人员"
435 "餐厅服务人员"
439 "其他餐饮服务人员"
440 "饭店、旅游及健身娱乐场所服务人员"
441 "饭店服务人员"
442 "旅游及公共游览场所服务人员"
443 "健身和娱乐场所服务人员"
449 "其他饭店、旅游及健身娱乐场所服务人员"
450 "输服务人员"
451 "公路道路运输服务人员"
452 "铁路客货运输服务人员"
453 "航空运输服务人员"
454 "水上运输服务人员"
459 "其他运输服务人员"
460 "医疗卫生辅助服务人员"
461 "医疗卫生辅助服务人员"
470 "社会服务和居民生活服务人员"
471 "社会中介服务人员"
472 "物业管理人员"
473 "供水、供热及生活燃供应服务人员"
474 "美容美发人员"
475 "摄影服务人员"
476 "验光配镜人员"
477 "洗染织补人员"
478 "浴池服务人员"
479 "印章刻字人员"
481 "日用机电产品维修人员"
482 "办公设备维修人员"
483 "保育、家庭服务人员"
484 "环境卫生人员"
485 "殡葬服务人员"
489 "其他社会服务和居民生活服务人员"
490 "其他商业、服务业人员"
499 "其他商业、服务业人员"
510 "种植业生产人员"
511 "大田作物生产人员"
512 "农业实验人员"
513 "园艺作物生产人员"
514 "热带作物生产人员"
515 "中药材生产人员"
516 "农副林特产品加工人员"
519 "其他种植业生产人员"
520 "林业生产及野生动植物保护人员"
521 "营造林人员"
522 "森林资源管护人员"
523 "野生动植物保护及自然保护区人员"
524 "木材采运人员"
529 "其他林业生产及野生动植物保护人员"
530 "畜牧业生产人员"
531 "家畜饲养人员"
532 "家禽饲养人员"
533 "蜜蜂饲养人员"
534 "实验动物饲养人员"
535 "动物疫病防治人员"
536 "草业生产人员"
539 "其他畜牧业生产人员"
540 "渔业生产人员"
541 "水产养殖人员"
542 "水产捕捞及有关人员"
543 "水产品加工人员"
549 "其他渔业生产人员"
550 "水利设施管理养护人员"
551 "河道、水库管养人员"
552 "农田灌排工程建设管理维护人员"
553 "水土保持作业人员"
554 "水文勘测作业人员"
559 "其他水利设施管理养护人员"
590 "其他农、林、牧、渔、水利业生产人员"
591 "农林专用机械操作人员"
592 "农村能源开发利用人员"
610 "勘测及矿物开采人员"
611 "地质勘查人员"
612 "测绘人员"
613 "矿物开采人员"
614 "矿物处理人员"
615 "钻井人员"
616 "石油、天然气开采人员"
617 "盐业主产人员"
619 "其他勘测及矿物开采人员"
620 "金属冶炼、轧制人员"
621 "炼铁人员"
622 "炼钢工人"
623 "铁合金冶炼人员"
624 "重有色金属冶炼人员"
625 "轻有色金属冶炼人员"
626 "稀贵金属冶炼人员"
627 "半导体材料制备人员"
628 "金属轧制人员"
629 "铸铁管人员"
631 "碳素制品生产人员"
632 "硬质合金生产人员"
639 "其他金属冶炼、轧制人员"
640 "化工产品生产人员"
641 "化工产品生产通用工艺人员"
642 "石油炼制生产人员"
643 "煤化工生产人员"
644 "化学肥料生产人员"
645 "无机化工产品生产人员"
646 "基本有机化工产品生产人员"
647 "合成树脂生产人员"
648 "合成橡胶生产人员"
649 "化学纤维生产人员"
651 "合成革生产人员"
652 "精细化工产品生产人员"
653 "信息记录材料生产人员"
654 "火药制造人员"
654 "炸药制造人员"
655 "林产化工产品生产人员"
656 "复合材料加工人员"
657 "日用化学品生产人员"
659 "其他化工产品生产人员"
660 "机械制造加工人员"
661 "机械冷加工人员"
662 "机械热加工人员"
663 "特种加工设备操作人员"
664 "冷作钣金加工人员"
665 "工件表面处理加工人员"
666 "磨料磨具制造加工人员"
667 "航天器件加工成型人员"
669 "其他机机制造加工人员"
670 "机电产品装配人员"
671 "基础件、部件装配人员"
672 "机械设备装配人员"
673 "动力设备装配人员"
674 "电气元件及设备装配人员"
675 "电子专用设备装配调试人员"
676 "仪器仪表装配人员"
677 "运输车辆装配人员"
678 "膜法水处理设备制造人员"
679 "医疗器械装配及假肢与矫形器制作人员"
681 "日用机械电器制作装配人员"
682 "五金制品制作、装配人员"
683 "装甲车辆装试人员"
684 "枪炮制作人员"
685 "弹制作人员"
686 "引信加工制作人员"
687 "火工品制作人员"
688 "防化器材制作人员"
689 "船舶制作人员"
691 "航空产品装配与调试人员"
692 "航空产品试验人员"
693 "导弹卫星装配测试人员"
694 "火箭发动机装配试验人员"
695 "航天器结构强度、温度、环境试验人员"
696 "靶场试验人员"
699 "其他机电产品装配人员"
710 "机械设备修理人员"
711 "机械设备维修人员"
712 "仪器仪表修理人员"
713 "民用航空器维修人员"
719 "其他机械设备修理人员"
720 "电力设备安装、运行、检修及供电人员"
721 "电力设备安装人员"
722 "发电运行值班人员"
723 "输电、配电、变电设备值班人员"
724 "电力设备检修人员"
725 "供用电人员"
726 "生活、生产电力设备安装、操作、修理人员"
729 "其他电力设备安装、运行、检修及供电人员"
730 "电子元器件与设备制造、装配、调试及维修人员"
731 "电子器件制造人员"
732 "电子元件制造人员"
733 "电池制造人员"
734 "电子设备装配、调试人员"
735 "电子产品维修人员"
739 "其他电子元器件与设备制造、装配、调试及维修人员"
740 "橡胶和塑料制品生产人员"
741 "橡胶制品生产人员"
742 "塑料制品生产人员"
749 "其他橡胶和塑料制品生产人员"
750 "纺织、针织、印染人员"
751 "纤维预处理人员"
752 "纺织人员"
753 "织造人员"
754 "针织人员"
755 "印染人员"
759 "其他纺织、针织、印染人员"
760 "裁剪、缝纫和皮革、毛皮制品加工制作人员"
761 "裁剪、缝纫人员"
762 "鞋帽制作人员"
763 "皮革、毛皮加工人员"
764 "缝纫制品再加工人员"
769 "其他裁剪、缝纫和皮革、毛皮制品加工制作人员"
770 "粮油、食品、饮料生产加工及饲料生产加工人员"
771 "粮油生产加工人员"
772 "制糖和糖制品加工人员"
773 "乳品、冷食品及罐头、饮料制作人员"
774 "酿酒、食品添加剂及调味品制作人员"
775 "粮油食品制作人员"
776 "屠宰加工人员"
777 "肉、蛋食品加工人员"
778 "饲料生产加工人员"
779 "其他粮油、食品、饮料生产加工及饲料生产加工人员"
780 "烟草及其制品加工人员"
781 "原烟复烤人员"
782 "卷烟生产人员"
783 "烟用醋酸纤维丝束滤棒制作人员"
789 "其他烟草及其制品加工人员"
790 "药品生产人员"
791 "合成药物制造人员"
792 "生物技术制药（品）人员"
793 "药物制剂人员"
794 "中药制药人员"
799 "其他药品生产人员"
810 "木材加工、人造板生产、木制品制作及制浆、造纸和纸制品生产加工人员"
811 "木材加工人员"
812 "人造板生产人员"
813 "木材制品制作人员"
814 "制浆人员"
815 "造纸人员"
816 "制品制作人员"
819 "其他木材加工、人造板生产、木制品制作及制浆、造纸和纸制品生产加工人员"
820 "建筑材料生产加工人员"
821 "水泥及水泥制品生产加工人员"
822 "墙体屋面材料生产人员"
823 "建筑防水密封材料生产人员"
824 "建筑保温及吸音材料生产人员"
825 "装饰石材生产人员"
826 "非金属矿及其制品生产加工人员"
827 "耐火材料生产人员"
829 "其他建筑材料生产加工人员"
830 "玻璃、陶瓷、搪瓷及其制品生产加工人员"
831 "玻璃熔制人员"
832 "玻璃纤维及制品生产人员"
833 "石英玻璃制品加工人员"
834 "陶瓷制品生产人员"
835 "搪瓷制品生产人员"
839 "其他玻璃、陶瓷、搪瓷及其制品生产加工人员"
840 "广播影视制品制作、播放及文物保护作业人员"
841 "影视制作制作人员"
842 "音像制品制作复制人员"
843 "广播影视舞台设备安装调试及运行操人员"
844 "电影放映人员"
845 "文物保护作业人员"
849 "其他广播影视制品制作、播放及文物保护作业人员"
850 "印刷人员"
851 "印前处理人员"
852 "印刷操作人员"
853 "印后制作人员"
859 "其他印刷人员"
860 "工艺、美术品制作人员"
861 "珠宝首饰加工制作人员"
862 "地毯制作人员"
863 "玩具制作人员"
864 "漆器工艺品制作人员"
865 "抽纱刺绣工艺品制作人员 "
866 "金属工艺品制作人员 "
867 "雕刻工艺品制作人员 "
868 "美术品制作人员"
869 "其他工艺、美术品制作人员"
870 "文化教育、体育用品制作人员"
871 "文教用品制作人员"
872 "体育用品制作人员"
873 "乐器制作人员"
879 "其他文化教育、体育用品制作人员"
880 "工程施工人员"
881 "土石方施工人员"
882 "砌筑人员"
883 "混凝土配制及制品加工人员"
884 "钢筋加工人员"
885 "施工架子搭设人员"
886 "工程防水人员"
887 "装饰装修人员"
888 "古建筑修建人员"
889 "筑路、养护、维修人员"
891 "工程设备安装人员"
899 "其他工程施工人员"
910 "运输设备操作人员及有关人员"
911 "公（道）路运输机械设备操作及有关人员"
912 "铁路、地铁运输机械设备操作及有关人员"
913 "民用航空设备操作及有关人员"
914 "水上运输设备操作及有关人员"
915 "起重装卸机械操作及有关人员"
919 "其他运输设备操作人员及有关人员"
920 "环境监测与废物处理人员"
921 "环境监测人员"
922 "海洋环境调查与监测人员"
923 "废物处理人员"
929 "其他环境监测与废物处理人员"
930 "检验、计量人员"
931 "检验人员"
932 "航空产品检验人员"
933 "航天器检验、测试人员"
934 "计量人员"
939 "其他检验、计量人员"
990 "其他生产、运输设备操作人员及有关人员"
991 "包装人员"
992 "机泵操作人员"
993 "简单体力劳动人员"
999 "不便分类的其他从业人员";
#delimit cr
lab values occup occup

gen occup01=occup
recode occup01 1/99=1 100/299=2 300/390=3 400/499=4 500/599=5 600/993=6 999=9
lab def occup01 0 "no job"                              ///
                1 "国家机关、党群组织、企业、事业单位负责人"    ///
                2 "专业技术人员"                ///
                3 "办事人员和有关人员"           ///
                4 "商业、服务业人员"             ///
                5 "农、林、牧、渔、水利业生产人员"        ///
                6 "生产、运输设备操作人员及有关人员"        ///
                9 "不便分类的其他从业人员"
lab values occup01 occup01

ren r211 ruemp
lab def ruemp 1 "在校学习" 2 "料理家务" 3 "离退休"  4 "丧失劳动能力" 5 "从未工作正在找工作" 6 "失去工作正在找工作" 
lab values ruemp ruemp
lab var ruemp "R211, 未工作者状况"
tab ruemp

sum r212
ren r212 foccup
lab var foccup "R212, last occupation"
lab values foccup occup

tab r22
lab def r22  1 "退休金" 2 "领取基本生活费" 3  "家庭其他成员供养" 4 "财产性收入" 5 "保险" 6 "其他"
lab values r22 r22
ren r22 uempinc
lab var uempinc "R22, not working: living source"

* marriage
tab r23
ren r23 marstat
lab def marstat 1 "未婚" 2 "初婚有配偶" 3 "再婚有配偶" 4 "离婚" 5 "丧偶"
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

lab var r251 "(15-50岁妇女F)fertility: # male"
lab var r252 "(15-50岁妇女)fertility: # female"
lab var r253 "(15-50岁妇女)fertility: # male alive now"
lab var r254 "(15-50岁妇女)fertility: # female alive now"

* birth in last year
lab var r261 "(15至50岁妇女)生育状况, past yr"
lab def r261 1 "未生育" 2 "有生育"
lab value r261 r261
tab1 r261 r265
tab r261 if r251==.

lab var r262 "(15至50岁妇女)上年生育状况, 生育时间_月"
lab var r263 "(15至50岁妇女)上年生育状况, 婴儿性别"
lab var r264 "(15至50岁妇女)上年生育的第二个孩子的状况, 生育时间_月"
lab var r265 "(15至50岁妇女)上年生育的第二个孩子的状况, 婴儿性别"

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

