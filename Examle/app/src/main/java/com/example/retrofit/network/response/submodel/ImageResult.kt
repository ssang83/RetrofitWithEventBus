package com.example.retrofit.network.response.submodel

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Kim Joonsung on 2019-08-12.
 */
class ImageResult : Parcelable {

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("img")
    @Expose
    var img: String? = null

    @SerializedName("id")
    @Expose
    var id: Int = 0

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.name)
        dest.writeString(this.img)
        dest.writeInt(this.id)
    }

    constructor() {}

    protected constructor(`in`: Parcel) {
        this.name = `in`.readString()
        this.img = `in`.readString()
        this.id = `in`.readInt()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ImageResult> = object : Parcelable.Creator<ImageResult> {
            override fun createFromParcel(source: Parcel): ImageResult {
                return ImageResult(source)
            }

            override fun newArray(size: Int): Array<ImageResult?> {
                return arrayOfNulls(size)
            }
        }
    }
}