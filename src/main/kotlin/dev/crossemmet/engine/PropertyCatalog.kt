package dev.crossemmet.engine

/** Versioned vocabulary. Collisions deliberately remain available as separate candidates. */
object PropertyCatalog {
    const val VERSION = 1
    val properties: List<PropertyDefinition> = buildList {
        fun group(kind: NumericKind, names: String) {
            names.split(Regex("\\s+")).filter(String::isNotBlank).forEach { add(PropertyDefinition(it, kind)) }
        }
        group(NumericKind.LENGTH, """
            width height min-width max-width min-height max-height
            inline-size block-size min-inline-size max-inline-size min-block-size max-block-size
            top right bottom left inset inset-inline inset-block inset-inline-start inset-inline-end inset-block-start inset-block-end
            margin margin-top margin-right margin-bottom margin-left margin-inline margin-block margin-inline-start margin-inline-end margin-block-start margin-block-end
            padding padding-top padding-right padding-bottom padding-left padding-inline padding-block padding-inline-start padding-inline-end padding-block-start padding-block-end
            gap row-gap column-gap flex-basis font-size letter-spacing word-spacing text-indent
            border-width border-top-width border-right-width border-bottom-width border-left-width
            border-inline-width border-block-width border-inline-start-width border-inline-end-width border-block-start-width border-block-end-width
            border-radius border-top-left-radius border-top-right-radius border-bottom-left-radius border-bottom-right-radius
            border-start-start-radius border-start-end-radius border-end-start-radius border-end-end-radius
            outline-width outline-offset column-width column-rule-width perspective
            scroll-margin scroll-margin-top scroll-margin-right scroll-margin-bottom scroll-margin-left
            scroll-margin-inline scroll-margin-block scroll-margin-inline-start scroll-margin-inline-end scroll-margin-block-start scroll-margin-block-end
            scroll-padding scroll-padding-top scroll-padding-right scroll-padding-bottom scroll-padding-left
            scroll-padding-inline scroll-padding-block scroll-padding-inline-start scroll-padding-inline-end scroll-padding-block-start scroll-padding-block-end
        """)
        group(NumericKind.NUMBER, "opacity flex-grow flex-shrink line-height aspect-ratio scale zoom fill-opacity stroke-opacity animation-iteration-count font-weight")
        group(NumericKind.INTEGER, "z-index order column-count orphans widows")
        group(NumericKind.TIME, "animation-duration animation-delay transition-duration transition-delay")
        group(NumericKind.ANGLE, "rotate offset-rotate")
        group(NumericKind.NONE, """
            display position float clear box-sizing overflow overflow-x overflow-y overflow-inline overflow-block
            color background background-color background-image background-size background-position background-repeat
            border border-style border-color border-top border-right border-bottom border-left
            border-inline border-block border-inline-start border-inline-end border-block-start border-block-end
            border-inline-style border-block-style border-inline-color border-block-color
            border-inline-start-style border-inline-end-style border-block-start-style border-block-end-style
            border-inline-start-color border-inline-end-color border-block-start-color border-block-end-color
            outline outline-style outline-color box-shadow text-shadow
            font font-family font-style font-variant font-feature-settings font-variation-settings
            text-align text-decoration text-transform text-overflow text-wrap text-wrap-mode text-wrap-style white-space word-break overflow-wrap hyphens
            writing-mode direction unicode-bidi vertical-align visibility cursor pointer-events user-select
            flex flex-direction flex-wrap flex-flow align-items align-content align-self justify-items justify-content justify-self place-items place-content place-self
            grid grid-template grid-template-columns grid-template-rows grid-template-areas grid-auto-columns grid-auto-rows grid-auto-flow grid-column grid-row grid-column-start grid-column-end grid-row-start grid-row-end grid-area
            container container-name container-type contain content-visibility contain-intrinsic-size contain-intrinsic-inline-size contain-intrinsic-block-size
            animation animation-name animation-timing-function animation-direction animation-fill-mode animation-play-state animation-timeline
            transition transition-property transition-timing-function transition-behavior
            transform transform-origin transform-style translate backface-visibility filter backdrop-filter
            object-fit object-position isolation mix-blend-mode appearance accent-color color-scheme
            list-style list-style-type list-style-position list-style-image resize
            scroll-behavior scroll-snap-type scroll-snap-align overscroll-behavior overscroll-behavior-inline overscroll-behavior-block
            touch-action will-change clip-path mask mask-image
        """)
    }.sortedBy { it.name }.also { definitions ->
        require(definitions.map { it.name }.distinct().size == definitions.size)
    }
}
