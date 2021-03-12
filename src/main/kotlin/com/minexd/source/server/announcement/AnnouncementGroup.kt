package com.minexd.source.server.announcement

class AnnouncementGroup(val id: String) {

    var announcements: MutableList<Announcement> = arrayListOf()
    var interval: Int = 60 // in seconds

}