//package dotty.using_directives.custom.utils;
//
//public class Spans {
//
//    private static final Long StartEndBits = 26L;
//    private static final Long StartEndMask = (1L << StartEndBits) - 1;
//    private static final Long SyntheticPointDelta = (1 << (64 - StartEndBits * 2)) - 1L;
//
//    /** The maximal representable offset in a span */
//    public static final Integer MaxOffset = StartEndMask.intValue();
//
//    /** Convert offset `x` to an integer by sign extending the original
//     *  field of `StartEndBits` width.
//     */
//    Integer offsetToInt(Integer x) {
//        return x << (32 - StartEndBits) >> (32 - StartEndBits);
//    }
//
///** A span indicates a range between a start offset and an end offset.
// *  Spans can be synthetic or source-derived. A source-derived span
// *  has in addition a point. The point lies somewhere between start and end. The point
// *  is roughly where the `^` would go if an error was diagnosed at that position.
// *  All quantities are encoded opaquely in a Long.
// */
//class Span {
//
//    Long coords;
//
//    public Span(Long coords) {
//        this.coords = coords;
//    }
//
//    /** Is this span different from NoSpan? */
//    Boolean exists() {
//        return !this.equals(NoSpan);
//    }
//
//    /** The start of this span. */
//    Integer start() {
//        assert(exists());
//        return ((Long)(coords & StartEndMask)).intValue();
//    }
//
//    /** The end of this span */
//    Integer end() {
//        assert(exists());
//        return ((Long)((coords >>> StartEndBits) & StartEndMask)).intValue();
//    }
//
//    /** The point of this span, returns start for synthetic spans */
//    Integer point() {
//        assert(exists());
//        Integer poff = pointDelta();
//        if (poff.equals(SyntheticPointDelta)) {
//            return start();
//        } else {
//            return start() + poff;
//        }
//    }
//
//    /** The difference between point and start in this span */
//    Integer pointDelta() {
//        return ((Long)(coords >>> (StartEndBits * 2))).intValue();
//    }
//
//    Span orElse(Span that) {
//        if (this.exists()) {
//            return this;
//        }
//        else {
//            return that;
//        }
//    }
//
//    /** The union of two spans. This is the least range that encloses
//     *  both spans. It is always a synthetic span.
//     */
//    Span union(Span that) {
//        if (!this.exists()) {
//            return that;
//        } else if (!that.exists()) {
//            return this;
//        }
//        else {
//            return Span(Integer.min(this.start(), that.start()), Integer.max(this.end(), that.end()), this.point());
//        }
//    }
//
//    /** Does the range of this span contain the one of that span? */
//    Boolean contains(Span that) {
//        return !that.exists() || exists() && (start() <= that.start() && end() >= that.end());
//    }
//
//    Boolean containsInner(Span span, Integer offset) {
//        return span.start() < offset && offset < span.end();
//    }
//
//    /** Does the range of this span overlap with the range of that span at more than a single point? */
//    Boolean overlaps(Span that) {
//        return exists() && that.exists() && (
//                containsInner(this, that.start())
//                        || containsInner(this, that.end())
//                        || containsInner(that, this.start())
//                        || containsInner(that, this.end())
//                        || this.start() == that.start() && this.end() == that.end()   // exact match in one point
//        );
//    }
//
//    /** Is this span synthetic? */
//    Boolean isSynthetic() {
//        return pointDelta() == SyntheticPointDelta.intValue();
//    }
//
//    /** Is this span source-derived? */
//    Boolean isSourceDerived() {
//        return !isSynthetic();
//    }
//
//    /** Is this a zero-extent span? */
//    Boolean isZeroExtent() {
//        return exists() && start().equals(end());
//    }
//
//    /** A span where all components are shifted by a given `offset`
//     *  relative to this span.
//     */
//    Span shift(Integer offset) {
//        if (exists()) {
//            return fromOffsets(start() + offset, end() + offset, pointDelta());
//        }
//        else {
//            return this;
//        }
//    }
//
//    /** The zero-extent span with start and end at the point of this span */
//    Span focus() {
//        if (exists()) {
//            return new Span(point());
//        } else {
//            return NoSpan;
//        }
//    }
//
//    /** The zero-extent span with start and end at the start of this span */
//    Span startPos() {
//        if (exists()) {
//            return new Span(start());
//        } else {
//            return NoSpan;
//        }
//    }
//
//    /** The zero-extent span with start and end at the end of this span */
//    Span endPos() {
//        if (exists()) {
//            return Span(end());
//        } else {
//            return NoSpan;
//        }
//    }
//
//    /** A copy of this span with a different start */
//    Span withStart(Integer start) {
//        if (exists() && isSynthetic()) {
//            return fromOffsets(start, this.end(), SyntheticPointDelta);
//        } else if (exists()) {
//            return fromOffsets(start, this.end(), this.point() - start);
//        }
//        else {
//            return this;
//        }
//    }
//
//    /** A copy of this span with a different end */
//    Span withEnd(Integer end) {
//        if (exists()) {
//            return fromOffsets(this.start(), end, pointDelta());
//        }
//        else {
//            return this;
//        }
//    }
//
//    /** A copy of this span with a different point */
//    Span withPoint(Integer point) {
//        if (exists()) {
//            return fromOffsets(this.start(), this.end(), point - this.start());
//        }
//        else {
//            return this;
//        }
//    }
//
//    /** A synthetic copy of this span */
//    Span toSynthetic() {
//        if (isSynthetic()) {
//            return this;
//        } else {
//            return new Span(start(), end());
//        }
//    }
//
//    @Override
//    public String toString() {
//        String left, right;
//        if (isSynthetic()) {
//            left = "<";
//            right = ">";
//        } else {
//            left = "[";
//            right = "]";
//        }
//        if (exists()) {
//            String str;
//            if (point().equals(start())) {
//                str = " ";
//            } else {
//                str = " " + point() + "..";
//            }
//            return left + start() + ".." + str + end() + right;
//        }
//        else {
//            return left + "no position" + right;
//        }
//    }
//
//    def ==(that: Span): Boolean = this.coords == that.coords
//    def !=(that: Span): Boolean = this.coords != that.coords
//}
//
//    private def fromOffsets(start: Int, end: Int, pointDelta: Int) =
//        //assert(start <= end || start == 1 && end == 0, s"$start..$end")
//        new Span(
//        (start & StartEndMask).toLong |
//        ((end & StartEndMask).toLong << StartEndBits) |
//        (pointDelta.toLong << (StartEndBits * 2)))
//
//        /** A synthetic span with given start and end */
//        def Span(start: Int, end: Int): Span =
//        fromOffsets(start, end, SyntheticPointDelta)
//
//        /** A source-derived span with given start, end, and point delta */
//        def Span(start: Int, end: Int, point: Int): Span = {
//        val pointDelta = (point - start) max 0
//        fromOffsets(start, end, if (pointDelta >= SyntheticPointDelta) 0 else pointDelta)
//        }
//
//        /** A synthetic zero-extent span that starts and ends at given `start`. */
//        def Span(start: Int): Span = Span(start, start)
//
//        /** A sentinel for a non-existing span */
//        val NoSpan: Span = Span(1, 0)
//
///** The coordinate of a symbol. This is either an index or
// *  a zero-range span.
// */
//class Coord(val encoding: Int) extends AnyVal {
//    def isIndex: Boolean = encoding > 0
//    def isSpan: Boolean = encoding <= 0
//    def toIndex: Int = {
//        assert(isIndex)
//                encoding - 1
//    }
//    def toSpan: Span = {
//        assert(isSpan)
//        if (this == NoCoord) NoSpan else Span(-1 - encoding)
//    }
//}
//
//    /** An index coordinate */
//    implicit def indexCoord(n: Int): Coord = new Coord(n + 1)
//        implicit def spanCoord(span: Span): Coord =
//        if (span.exists) new Coord(-(span.point + 1))
//        else NoCoord
//
//        /** A sentinel for a missing coordinate */
//        val NoCoord: Coord = new Coord(0)
//        }
