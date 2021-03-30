package com.musicalarm.model

import java.io.Serializable

open class AlarmSong : ClassLoader() , Serializable{
    var id: Int?= -1
    var title: String?=""
    var uri: String?=""
}