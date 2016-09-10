package io.github.ryanhoo.music.data.source;

import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.data.model.PlayList;
import rx.Observable;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:52 PM
 * Desc: AppContract
 */
/* package */ interface AppContract {

    // Play List

    Observable<List<PlayList>> playLists();

    Observable<PlayList> create(PlayList playList);

    Observable<PlayList> update(PlayList playList);

    Observable<PlayList> delete(PlayList playList);

    // Folder

    Observable<List<Folder>> folders();

    Observable<Folder> create(Folder folder);

    Observable<List<Folder>> create(List<Folder> folders);

    // Observable<Folder> update(Folder folder);

    Observable<Folder> delete(Folder folder);
}
