package com.edit.photo.foto.presentation

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat.startActivity
import com.edit.photo.foto.R
import com.themanufacturers.domain.model.ImageProject
import com.themanufacturers.domain.model.Sticker
import com.themanufacturers.domain.model.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class EditView(val ctx: Context, attributeSet: AttributeSet): SurfaceView(ctx,attributeSet) {

    lateinit var project : ImageProject

    var uri: Uri? = null
    var paused = false
    private var millis = 0
    private var bitmap: Bitmap? = null
    private val paintB = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCut = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 15f
        color = Color.BLACK
        pathEffect = DashPathEffect(floatArrayOf(35f,25f),0f)
    }
    private val paintOutOfCut = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#59000000")
    }
    private val paintT = Paint().apply {
        textSize = 40f
        color = Color.WHITE
    }
    private lateinit var resizeCallback: ResizeCallback
    private lateinit var textCallback: TextCallback
    private lateinit var invalidateCallback: SelectionCallback
    var ind = 0
    private var first = true

    private lateinit var list: MutableList<Sticker>

    private var data = arrayListOf(
        R.drawable.t1,
        R.drawable.t2,
        R.drawable.t3,
        R.drawable.t4,
        R.drawable.t5,
        R.drawable.t6,
        R.drawable.t7,
        R.drawable.t8,
        R.drawable.t9,
        R.drawable.t10,
        R.drawable.t11,
        R.drawable.t12,
        R.drawable.t13,
        R.drawable.t14,
        R.drawable.t15,
        R.drawable.t16,
    ).map { BitmapFactory.decodeResource(ctx.resources,it) }.toMutableList()

    private var cancel = BitmapFactory.decodeResource(ctx.resources, R.drawable.cancel)
    private var scale = BitmapFactory.decodeResource(ctx.resources, R.drawable.scale)
    private var turn = BitmapFactory.decodeResource(ctx.resources, R.drawable.turn)

    fun setTextCallback(callback: TextCallback) {
        this.textCallback = callback
    }

    fun setResizeCallback(callback: ResizeCallback) {
        this.resizeCallback = callback
    }

    fun setSelectionCallback(callback: SelectionCallback) {
        this.invalidateCallback = callback
    }

    private var bitmapTmp: Bitmap? = null

    init {
        turn = Bitmap.createScaledBitmap(turn,turn.width/2,turn.height/2,true)
        scale = Bitmap.createScaledBitmap(scale,scale.width/2,scale.height/2,true)
        cancel = Bitmap.createScaledBitmap(cancel, cancel.width/2,cancel.height/2,true)
        for(i in data.indices) data[i] = Bitmap.createScaledBitmap(data[i],data[i].width/5,data[i].height/5,true)
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                list = project.models
                if(project.rectCut!=null) rectCut = RectF(project.rectCut!![0],project.rectCut!![1],project.rectCut!![2],project.rectCut!![3],)
                uri = Uri.parse(project.imageUri)
                if (project.bitmap==null) {
                    if(first) {
                        first = false
                        CoroutineScope(Dispatchers.IO).launch {
                            bitmap = Images.Media.getBitmap(ctx.contentResolver,uri)
                            bitmap = Bitmap.createBitmap(
                                bitmap!!,
                                bitmap!!.width / 2 - min(bitmap!!.width / 2, width / 2),
                                bitmap!!.height / 2 - min(bitmap!!.height / 2, height / 2),
                                min(bitmap!!.width / 2, width / 2) * 2,
                                min(bitmap!!.height / 2, height / 2) * 2
                            )
                        }.invokeOnCompletion {
                            bitmapTmp = Bitmap.createBitmap(bitmap!!)
                            if(project.rectCut!=null) {
                                cut()
                            } else {
                                resizeCallback.resize(bitmap!!,true)
                            }
                        }
                    }
                } else {
                    bitmap = BitmapFactory.decodeByteArray(project.bitmap,0,project.bitmap!!.size,BitmapFactory.Options().apply {
                        outHeight = project.height
                        outWidth = project.width
                    })
                    bitmapTmp = Bitmap.createBitmap(bitmap!!)
                    if(project.rectCut!=null) {
                        cut()
                    } else {
                        resizeCallback.resize(bitmap!!,true)
                    }
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                paused = true
            }

        })
        val updateThread = Thread {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    if (!paused) {
                        update.run()
                        millis ++
                    }
                }
            }, 1000, 40)
        }

        updateThread.start()
    }

    private var heightC = 0
    private var widthC = 0
    private var rectCut: RectF? = null

    private fun cut() {
        bitmapTmp = Bitmap.createBitmap(
            bitmap!!, rectCut!!.left.toInt(), rectCut!!.top.toInt(),
            rectCut!!.right.toInt()-rectCut!!.left.toInt(),
            rectCut!!.bottom.toInt()-rectCut!!.top.toInt(),
            null,
            true
        )
        resizeCallback.resize(bitmapTmp!!,true)
        ind = 0
        invalidateCallback.invalidate(ind)
    }

    fun enableCutMode() {
        if(ind!=1) {
            ind = 1
            if(rectCut==null) rectCut = RectF((0.1f*widthC),(0.1f*heightC),(widthC*0.9f),(heightC*0.9f))
            if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height)) resizeCallback.resize(bitmap!!,false)
            //Log.d("TAG","${bitmap!!.width} ${bitmapTmp!!.width}")
        } else {
            ind = 0
            resizeCallback.resize(bitmapTmp!!,true)
        }
    }

    fun cleanTextFocus() {
        for(i in project.texts) i.edit = false
        if(ind==3) ind = 0
        invalidateCallback.invalidate(ind)
    }

    private fun drawOutOffCut(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f,0f)
        path.lineTo(canvas.width.toFloat(),0f)
        path.lineTo(canvas.width.toFloat(),canvas.height.toFloat())
        path.lineTo(0f,canvas.height.toFloat())
        path.lineTo(0f,0f)
        path.fillType = Path.FillType.EVEN_ODD
        path.addPath(Path().apply { addRect(rectCut!!,Path.Direction.CW) })
        canvas.drawPath(path,paintOutOfCut)
    }

    private var tx = -1f
    private var ty = -1f
    private var cutMove = 0 // 1 top | 2 right | 3 bottom | 4 left

    private var offsetX = 0f
    private var offsetY = 0f
    private var stickerInd = -1
    private var move = false
    private var scaled = false
    private var turned = false

    private var prevX = -1f
    private var prevY = -1f

    public var textInd = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        prevX = tx
        prevY = ty
        tx = event!!.x
        ty = event.y
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                if(ind==1) {
                    if(abs(event.x-rectCut!!.left)<=15 || abs(event.y-rectCut!!.top)<=15
                        || abs(event.y-rectCut!!.bottom)<=15 || abs(event.x-rectCut!!.right)<=15
                        ) {
                        if(abs(event.x-rectCut!!.left)<=15) cutMove = 4
                        if(abs(event.x-rectCut!!.right)<=15) cutMove = 2
                        if(abs(event.y-rectCut!!.bottom)<=15) cutMove = 3
                        if(abs(event.y-rectCut!!.top)<=15) cutMove = 1
                    } else if(
                        event.x>=rectCut!!.left && event.x<=rectCut!!.right && event.y>=rectCut!!.top && event.y<=rectCut!!.bottom
                    ) {
                        ind = 0
                        resizeCallback.resize(bitmapTmp!!,true)
                        invalidateCallback.invalidate(ind)
                    } else {
                        cut()
                    }
                } else {
                    move = false
                    var removeInd = -1
                    var removedTextInd = -1
                    for(i in list.indices) {
                        val t = list[i]
                        var x1 = t.x
                        var y1 = t.y
                        if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
                            x1 -= rectCut!!.left
                            y1 -= rectCut!!.top
                        }
                        val rect = getRect(t)
                        val cancelX = rect.right-cancel.width/2f
                        val cancelY = rect.top-cancel.height/2f

                        val scaleX = rect.right-scale.width/2f
                        val scaleY = rect.bottom-scale.height/2f

                        val turnX = rect.left-scale.width/2f
                        val turnY = rect.bottom-scale.height/2f

                        if(tx>=cancelX && tx<=cancelX+cancel.width && ty>=cancelY && ty<=cancelY+cancel.height) {
                            removeInd = i
                            textInd = -1
                            break
                        }
                        else if(tx>=scaleX && tx<=scaleX+scale.width && ty>=scaleY && ty<=scaleY+scale.height) {
                            stickerInd = i
                            textInd = -1
                            scaled = true
                            break
                        }
                        else if(tx>=turnX && tx<=turnX+scale.width && ty>=turnY && ty<=turnY+scale.height) {
                            stickerInd = i
                            textInd = -1
                            turned = true
                            break
                        }
                        else if(tx>=x1 && tx<=x1+data[t.ind].width && ty>=y1 && ty<=y1+data[t.ind].height && t.edit) {
                            stickerInd = i
                            textInd = -1
                            offsetX = t.x-tx
                            offsetY = t.y-ty
                            //move = true
                           // t.edit = true
                            break
                        }
                    }
                    if(removeInd!=-1) list.removeAt(removeInd)
                    if(stickerInd!=-1) {
                        for(i in list.indices) {
                            if(stickerInd!=i) list[i].edit = false
                        }
                    }
                    for(i in project.texts.indices) {
                        val t = project.texts[i]
                        paintT.apply {
                            textSize=t.scale*TEXT_CONST
                        }
                        var x1 = t.x
                        var y1 = t.y
                        if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
                            x1 -= rectCut!!.left
                            y1 -= rectCut!!.top
                        }
                        val rect = getRectText(t)
                        val cancelX = rect.right-cancel.width/2f
                        val cancelY = rect.top-cancel.height/2f

                        val scaleX = rect.right-scale.width/2f
                        val scaleY = rect.bottom-scale.height/2f

                        if(tx>=cancelX && tx<=cancelX+cancel.width && ty>=cancelY && ty<=cancelY+cancel.height) {
                            removedTextInd = i
                            stickerInd = -1
                            break
                        }
                        else if(tx>=scaleX && tx<=scaleX+scale.width && ty>=scaleY && ty<=scaleY+scale.height) {
                            textInd = i
                            stickerInd = -1
                            scaled = true
                            break
                        }
                        else if(tx>=x1-20 && tx<=x1+paintT.measureText(t.text)+20 && ty>=y1-15 && ty<=y1+paintT.textSize+15 && t.edit) {
                            stickerInd = -1
                            textInd = i
                            offsetX = t.x-tx
                            offsetY = t.y-ty
                            //move = true
                            // t.edit = true
                            break
                        }
                    }
                    if(removedTextInd!=-1) project.texts.removeAt(removedTextInd)
                 }

            }
            MotionEvent.ACTION_MOVE -> {
                if(ind==1) {
                    if(cutMove!=0) {
                        if(cutMove==1) rectCut!!.top = max(0f, min(heightC.toFloat(),event.y))
                        if(cutMove==2) rectCut!!.right = max(0f,min(event.x,widthC.toFloat()))
                        if(cutMove==3) rectCut!!.bottom = max(0f, min(heightC.toFloat(),event.y))
                        if (cutMove==4) rectCut!!.left = max(0f,min(event.x,widthC.toFloat()))
                    }
                } else {
                    move = false
                    if(stickerInd!=-1) {
                        textInd = -1
                        if(!turned && !scaled) {
                            list[stickerInd].x = tx+offsetX
                            list[stickerInd].y = ty+offsetY
                            move = true
                        } else if(turned) {
                            list[stickerInd].angle += (tx-prevX)/10
                        }
                        else {
                            list[stickerInd].scale += (ty-prevY)/100
                            list[stickerInd].scale = max(list[stickerInd].scale,0f)
                        }
                    } else if(textInd!=-1) {
                        stickerInd = -1
                        if(!turned && !scaled) {
                            project.texts[textInd].x = tx + offsetX
                            project.texts[textInd].y = ty + offsetY
                            move = true
                        } else {
                            project.texts[textInd].scale += (ty-prevY)/100
                            project.texts[textInd].scale = max(project.texts[textInd].scale,0f)
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                for(i in list) {
                    var x1 = i.x
                    var y1 = i.y
                    if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
                        x1 -= rectCut!!.left
                        y1 -= rectCut!!.top
                    }
                    if(tx>=x1 && tx<=x1+data[i.ind].width && ty>=y1 && ty<=y1+data[i.ind].height) {
                        textInd = -1
                        if(!move) i.edit = !i.edit
                        break
                    }
                }
                for(j in project.texts.indices) {
                    val i = project.texts[j]
                    var x1 = i.x
                    var y1 = i.y
                    if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
                        x1 -= rectCut!!.left
                        y1 -= rectCut!!.top
                    }
                    if(tx>=x1-20 && tx<=x1+paintT.measureText(i.text)+20 && ty>=y1-15 && ty<=y1+paintT.textSize+15) {
                        stickerInd = -1
                        if(!move) {
                            i.edit = !i.edit
                            if(i.edit) {
                                textInd = j
                                textCallback.open()
                            }
                        }
                        break
                    }
                }
                move = false
                cutMove = 0
                tx = -1f
                ty = -1f
                scaled = false
                turned = false
                stickerInd = -1
            }
        }
        return true
    }

    fun addSticker(ind: Int) {
        list.add(
            Sticker(
                ind,
                widthC / 2f,
                height / 2f,
                1f,
                0f,
                false
            )
        )
    }
    fun addText() {
        project.texts.add(
            Text(
                "",
                widthC / 2f,
                heightC / 2f,
                1f,
                0f,
                true,
                "#FFFFFF"
            )
        )
        textInd = project.texts.size-1
    }

    private val update = Runnable {
        try {
            val canvas = holder.lockCanvas()
            heightC = canvas.height
            widthC = canvas.width
            if(bitmap!=null) {
                if(ind==1) {
                    canvas.drawBitmap(bitmap!!,canvas.width/2f-bitmap!!.width/2f,
                        canvas.height/2f-bitmap!!.height/2f,paintB)
                } else {
                    canvas.drawBitmap(bitmapTmp!!,canvas.width/2f-bitmapTmp!!.width/2f,
                        canvas.height/2f-bitmapTmp!!.height/2f,paintB)
                }
                var editSticker = false
                for(i in list) {
                    var x1 = i.x
                    var y1 = i.y
                    if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
                        x1 -= rectCut!!.left
                        y1 -= rectCut!!.top
                    }
                    val matrix = Matrix()
                    matrix.preRotate(i.angle, data[i.ind].width/2f,data[i.ind].height/2f)
                    matrix.preScale(i.scale,i.scale)
                    matrix.postTranslate(x1,y1)
                    canvas.drawBitmap(data[i.ind],matrix,paintB)
                    if(i.edit) {
                        editSticker = true
                        val p = Path()
                        val rf = getRect(i)
                        p.addRect(rf,Path.Direction.CW)
                        canvas.drawPath(p,paintCut)
                        canvas.drawBitmap(cancel, rf.right-cancel.width/2f,rf.top-cancel.height/2f,paintB)
                        canvas.drawBitmap(scale, rf.right-cancel.width/2f,rf.bottom-cancel.height/2f,paintB)
                        canvas.drawBitmap(turn, rf.left-turn.width/2f,rf.bottom-turn.height/2f,paintB)
                    }
                }
                var editText = false
                for(j in project.texts.indices) {
                    val i = project.texts[j]
                    if(j!=textInd) i.edit = false
                    var x1 = i.x
                    var y1 = i.y
                    if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
                        x1 -= rectCut!!.left
                        y1 -= rectCut!!.top
                    }
                    //Log.d("TAG",i.color+" |||")
                    canvas.drawText(i.text,x1,y1,paintT.apply {
                        textSize=i.scale*TEXT_CONST
                        color = Color.parseColor(i.color)
                    })
                    if(i.edit) {
                        editText = true
                        val p = Path()
                        val rf = getRectText(i)
                        p.addRect(rf,Path.Direction.CW)
                        canvas.drawPath(p,paintCut)
                        canvas.drawBitmap(cancel, rf.right-cancel.width/2f,rf.top-cancel.height/2f,paintB)
                        canvas.drawBitmap(scale, rf.right-cancel.width/2f,rf.bottom-cancel.height/2f,paintB)
                        //canvas.drawBitmap(turn, rf.left-turn.width/2f,rf.bottom-turn.height/2f,paintB)
                    }
                }
                var tmp = 0
                if(editSticker) tmp = 2
                if(editText) tmp = 3
                if(ind==1) tmp = 1
                if(ind!=tmp) {
                    invalidateCallback.invalidate(tmp)
                    ind = tmp
                }
                if(ind==1) {
                    drawOutOffCut(canvas)
                    canvas.drawPath(Path().apply { addRect(rectCut!!,Path.Direction.CW) },paintCut)
                }

            }
            holder.unlockCanvasAndPost(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getRect(i: Sticker): RectF {
        var x1 = i.x
        var y1 = i.y
        if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
            x1 -= rectCut!!.left
            y1 -= rectCut!!.top
        }
        val matrix = Matrix()
        matrix.postRotate(i.angle,data[i.ind].width/2f*i.scale,data[i.ind].height/2f*i.scale)
        matrix.postTranslate(x1,y1)
        var rect = RectF(0f,0f,data[i.ind].width.toFloat()*i.scale,data[i.ind].height.toFloat()*i.scale)
        val rectangleCorners = floatArrayOf(
            rect.left, rect.top,  //left, top
            rect.right, rect.top,  //right, top
            rect.right, rect.bottom,  //right, bottom
            rect.left, rect.bottom //left, bottom
        )
        matrix.mapPoints(rectangleCorners)
        val points = arrayOf(
            Point(rectangleCorners[0].toInt(), rectangleCorners[1].toInt()),
            Point(rectangleCorners[2].toInt(), rectangleCorners[3].toInt()),
            Point(rectangleCorners[4].toInt(), rectangleCorners[5].toInt()),
            Point(rectangleCorners[6].toInt(), rectangleCorners[7].toInt())
        )
        var top = Float.MAX_VALUE
        var let = Float.MAX_VALUE
        var bottom = 0f
        var right = 0f
        for(j in points) {
            top = min(top,j.y.toFloat())
            let = min(let,j.x.toFloat())
            bottom = max(bottom,j.y.toFloat())
            right = max(right,j.x.toFloat())
        }
        rect = RectF(
            let,top,right,bottom
        )
        return rect
    }

    private fun getRectText(i: Text): RectF {
        var x1 = i.x
        var y1 = i.y
        if((bitmap!!.width!=bitmapTmp!!.width || bitmap!!.height!=bitmapTmp!!.height) && ind!=1) {
            x1 -= rectCut!!.left
            y1 -= rectCut!!.top
        }
        y1-=paintT.textSize
        x1-=20
        val matrix = Matrix()
        matrix.postTranslate(x1,y1)
        var rect = RectF(0f,0f,paintT.measureText(i.text)+40,paintT.textSize+30)
        val rectangleCorners = floatArrayOf(
            rect.left, rect.top,  //left, top
            rect.right, rect.top,  //right, top
            rect.right, rect.bottom,  //right, bottom
            rect.left, rect.bottom //left, bottom
        )
        matrix.mapPoints(rectangleCorners)
        val points = arrayOf(
            Point(rectangleCorners[0].toInt(), rectangleCorners[1].toInt()),
            Point(rectangleCorners[2].toInt(), rectangleCorners[3].toInt()),
            Point(rectangleCorners[4].toInt(), rectangleCorners[5].toInt()),
            Point(rectangleCorners[6].toInt(), rectangleCorners[7].toInt())
        )
        var top = Float.MAX_VALUE
        var let = Float.MAX_VALUE
        var bottom = 0f
        var right = 0f
        for(j in points) {
            top = min(top,j.y.toFloat())
            let = min(let,j.x.toFloat())
            bottom = max(bottom,j.y.toFloat())
            right = max(right,j.x.toFloat())
        }
        rect = RectF(
            let,top,right,bottom
        )
        return rect
    }

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun share(callback: (ImageProject)->Unit) {
        for(i in list) i.edit = false
        for(i in project.texts) i.edit = false
        if (ind==1) {
            ind = 0
            rectCut = null
        }
        val bitmap1 = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(this,bitmap1, {
            val share = Intent(Intent.ACTION_SEND)
            share.setType("image/jpeg")
            val name = "${ctx.packageName}${project.id}"
            val values = ContentValues()
            values.put(Images.Media.DISPLAY_NAME, name)
            values.put(Images.Media.MIME_TYPE, "image/png")
            val uri = ctx.contentResolver.insert(
                Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            val outStream: OutputStream
            try {
                outStream = ctx.contentResolver.openOutputStream(uri!!)!!
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.close()
            } catch (e: java.lang.Exception) {
                System.err.println(e.toString())
            }
            val byteStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, byteStream)
            val bitmapBytes = byteStream.toByteArray()
            project.bitmap = bitmapBytes
            val byteStream1 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 0, byteStream1)
            val bitmapBytes1 = byteStream1.toByteArray()
            project.preview = bitmapBytes1
            project.rectCut = if(rectCut==null) null else mutableListOf(rectCut!!.left,rectCut!!.top,rectCut!!.right,rectCut!!.bottom)
            project.width = width
            project.height = height
            callback(project)
            share.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(ctx,Intent.createChooser(share, "Share Image"), Bundle())
        },handler)
    }

    fun saveToUri(uri: Uri, callback: (ImageProject) -> Unit) {
        for(i in list) i.edit = false
        for(i in project.texts) i.edit = false
        if (ind==1) {
            ind = 0
            rectCut = null
        }
        val bitmap1 = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(this,bitmap1, {
            val outStream: OutputStream
            try {
                outStream = ctx.contentResolver.openOutputStream(uri)!!
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, outStream)
                outStream.close()
            } catch (e: java.lang.Exception) {
                System.err.println(e.toString())
            }
            val byteStream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 0, byteStream)
            val bitmapBytes = byteStream.toByteArray()

            val byteStream1 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 0, byteStream1)
            val bitmapBytes1 = byteStream1.toByteArray()
            project.preview = bitmapBytes1
            project.bitmap = bitmapBytes
            project.rectCut = if(rectCut==null) null else mutableListOf(rectCut!!.left,rectCut!!.top,rectCut!!.right,rectCut!!.bottom)
            project.width = width
            project.height = height
            callback(project)
        },handler)
    }
    companion object {
        interface SelectionCallback {
            fun invalidate(ind:Int)
        }
        interface ResizeCallback {
            fun resize(b: Bitmap,small: Boolean)
        }
        interface TextCallback {
            fun open()
        }
        const val TEXT_CONST = 30

      }
}

