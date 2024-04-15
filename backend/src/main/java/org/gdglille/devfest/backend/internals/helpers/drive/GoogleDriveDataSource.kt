package org.gdglille.devfest.backend.internals.helpers.drive

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission

private const val PageSize = 10

class GoogleDriveDataSource(private val service: Drive) : DriveDataSource {
    override fun findDriveByName(name: String): String? {
        val drives = service.drives().list().apply {
            pageSize = PageSize
            fields = "nextPageToken, drives(id, name)"
        }.execute()
        return drives.drives.find { it.name == name }?.id
    }

    override fun findFolderByName(name: String, parentId: String?, driveId: String?): String? {
        return service.files().list().apply {
            if (driveId != null) {
                this.driveId = driveId
                includeItemsFromAllDrives = true
                supportsAllDrives = true
                corpora = "drive"
            }
            pageSize = PageSize
            fields = "nextPageToken, files(id)"
            this.q = "name='$name'${parentId?.let { " and parents='$parentId'" } ?: run { "" }}"
        }.execute().files.firstOrNull()?.id
    }

    override fun findFileByName(name: String, parentId: String?, driveId: String?): String? {
        return service.files().list().apply {
            if (driveId != null) {
                this.driveId = driveId
                includeItemsFromAllDrives = true
                supportsAllDrives = true
                corpora = "drive"
            }
            pageSize = PageSize
            fields = "nextPageToken, files(id)"
            this.q = "name='$name'${parentId?.let { " and parents='$parentId'" } ?: run { "" }}"
        }.execute().files.firstOrNull()?.id
    }

    override fun createFolder(name: String, parentId: String?, driveId: String?): String {
        val fileMetadata = File().apply {
            this.name = name
            mimeType = "application/vnd.google-apps.folder"
            if (driveId != null) {
                parents = listOf(driveId)
            }
        }
        val folderId = service.files().create(fileMetadata).apply {
            supportsAllDrives = true
            fields = "id"
        }.execute().id
        if (parentId != null) {
            moveFile(folderId, parentId)
        }
        return folderId
    }

    override fun copyFile(name: String, fileId: String, driveId: String?): String {
        val file = File().apply {
            this.name = name
            this.mimeType = "application/vnd.google-apps.spreadsheet"
            if (driveId != null) {
                parents = listOf(driveId)
            }
        }
        return service.files().copy(fileId, file).apply {
            supportsAllDrives = true
            fields = "id"
        }.execute().id
    }

    override fun moveFile(fileId: String, folderId: String): List<String> {
        val file = service.files()[fileId].apply {
            supportsAllDrives = true
            fields = "parents"
        }.execute()
        return service.files().update(fileId, null).apply {
            supportsAllDrives = true
            addParents = folderId
            removeParents = file.parents.joinToString(",")
            fields = "parents"
        }.execute().parents
    }

    override fun grantPermission(fileId: String, email: String): String? {
        val userPermission: Permission = Permission().apply {
            type = "user"
            role = "writer"
            emailAddress = email
        }
        return service.permissions().create(fileId, userPermission).apply {
            supportsAllDrives = true
            fields = "id"
        }.execute().id
    }
}
