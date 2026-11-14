package dev.crossemmet.engine

data class KeywordShortcut(val abbreviation: String, val property: String, val value: String)

/** Explicit aliases avoid guessing between keywords that share initials. */
object KeywordCatalog {
    val shortcuts: List<KeywordShortcut> = buildList {
        fun group(property: String, entries: String) {
            entries.split(' ').forEach { entry ->
                val (alias, value) = entry.split('=')
                add(KeywordShortcut(alias, property, value))
            }
        }
        group("display", "dn=none db=block di=inline dib=inline-block df=flex dif=inline-flex dg=grid dig=inline-grid dt=table dtc=table-cell dtr=table-row dc=contents")
        group("position", "posr=relative posa=absolute posf=fixed poss=static posst=sticky")
        group("justify-content", "jcc=center jcfs=flex-start jcfe=flex-end jcs=start jce=end jcsb=space-between jcsa=space-around jcse=space-evenly jcst=stretch")
        group("align-items", "aic=center aifs=flex-start aife=flex-end ais=start aie=end aist=stretch aib=baseline")
        group("align-content", "acc=center acfs=flex-start acfe=flex-end acs=start ace=end acsb=space-between acsa=space-around acse=space-evenly acst=stretch acb=baseline")
        group("align-self", "asa=auto asc=center asfs=flex-start asfe=flex-end ass=start ase=end asst=stretch asb=baseline")
        group("justify-items", "jic=center jis=start jie=end jist=stretch")
        group("justify-self", "jsa=auto jsc=center jss=start jse=end jsst=stretch")
        group("place-items", "pic=center pist=stretch")
        group("place-content", "pcc=center pcsb=space-between pcsa=space-around pcse=space-evenly")
        group("flex-direction", "fdr=row fdrr=row-reverse fdc=column fdcr=column-reverse")
        group("flex-wrap", "fwn=nowrap fww=wrap fwwr=wrap-reverse")
        group("flex", "fxa=auto fxn=none")
        group("grid-auto-flow", "gafr=row gafc=column gafd=dense")
        group("text-align", "tac=center tal=left tar=right taj=justify tas=start tae=end")
        group("text-transform", "ttu=uppercase ttl=lowercase ttc=capitalize ttn=none")
        group("text-decoration", "tdn=none tdu=underline tdo=overline tdl=line-through")
        group("text-overflow", "toe=ellipsis toc=clip")
        group("text-wrap", "twb=balance twp=pretty tww=wrap twn=nowrap")
        group("white-space", "wsn=normal wsnw=nowrap wsp=pre wspw=pre-wrap wspl=pre-line wsb=break-spaces")
        group("word-break", "wbn=normal wbba=break-all wbka=keep-all")
        group("overflow-wrap", "own=normal owbw=break-word owa=anywhere")
        group("font-style", "fsi=italic fsn=normal fso=oblique")
        group("font-weight", "fwb=bold fwnm=normal fwbr=bolder fwlr=lighter")
        group("overflow", "oh=hidden oa=auto os=scroll ov=visible oc=clip")
        group("overflow-x", "oxh=hidden oxa=auto oxs=scroll oxv=visible oxc=clip")
        group("overflow-y", "oyh=hidden oya=auto oys=scroll oyv=visible oyc=clip")
        group("box-sizing", "bxbb=border-box bxcb=content-box")
        group("visibility", "vh=hidden vv=visible vc=collapse")
        group("cursor", "cp=pointer cd=default ca=auto cm=move cna=not-allowed ct=text cw=wait cc=crosshair cg=grab cgg=grabbing")
        group("pointer-events", "pen=none pea=auto")
        group("user-select", "usn=none usa=auto ust=text usall=all")
        group("object-fit", "ofc=cover ofct=contain off=fill ofn=none ofsd=scale-down")
        group("background-repeat", "bgrn=no-repeat bgrr=repeat bgrx=repeat-x bgry=repeat-y")
        group("background-size", "bgsc=cover bgsct=contain")
        group("border-style", "bdsn=none bdss=solid bdsd=dashed bdsdt=dotted bdsdb=double")
        group("float", "fl=left fr=right fn=none")
        group("clear", "cl=left cr=right cb=both cn=none")
        group("resize", "rn=none rb=both rh=horizontal rv=vertical")
        group("list-style-type", "lstn=none lstd=disc lstc=circle lsts=square")
        group("appearance", "apn=none apa=auto")
        group("isolation", "isi=isolate isa=auto")
        group("scroll-behavior", "sbs=smooth sba=auto")
        group("container-type", "ctis=inline-size cts=size ctn=normal")
        group("content-visibility", "cva=auto cvh=hidden cvv=visible")
        group("contain", "cntc=content cnts=strict cntn=none")
    }.sortedBy { it.abbreviation }.also { entries ->
        require(entries.map { it.abbreviation }.distinct().size == entries.size)
        require(entries.all { entry -> PropertyCatalog.properties.any { it.name == entry.property } })
    }
    val byAbbreviation: Map<String, KeywordShortcut> = shortcuts.associateBy { it.abbreviation }
}
