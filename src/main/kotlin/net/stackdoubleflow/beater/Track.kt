package net.stackdoubleflow.beater

import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import java.awt.geom.Line2D

data class Line(val startPos: Vec2f, val endPos: Vec2f) {
    private fun toLine2D() = Line2D.Float(startPos.x, startPos.y, endPos.x, endPos.y)

    fun intersects(other: Line) =
        toLine2D().intersectsLine(other.toLine2D())
}

data class Track(val startPos: Vec3d, val checkpoints: List<Line>, val endLine: Line)
