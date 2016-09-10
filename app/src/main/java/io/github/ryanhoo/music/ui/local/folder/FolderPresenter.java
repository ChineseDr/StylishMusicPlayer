package io.github.ryanhoo.music.ui.local.folder;

import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.source.AppRepository;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 11:38 PM
 * Desc: FolderPresenter
 */
public class FolderPresenter implements FolderContract.Presenter {

    private FolderContract.View mView;
    private AppRepository mRepository;
    private CompositeSubscription mSubscriptions;

    public FolderPresenter(AppRepository repository, FolderContract.View view) {
        mView = view;
        mRepository = repository;
        mSubscriptions = new CompositeSubscription();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadFolders();
    }

    @Override
    public void unsubscribe() {
        mView = null;
        mSubscriptions.clear();
    }

    @Override
    public void loadFolders() {
        Subscription subscription = mRepository.folders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Folder>>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(List<Folder> folders) {
                        mView.onFoldersLoaded(folders);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void addFolders(List<File> folders, final List<Folder> existedFolders) {
        Subscription subscription = Observable.from(folders)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        for (Folder folder : existedFolders) {
                            if (file.getAbsolutePath().equals(folder.getPath())) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .flatMap(new Func1<File, Observable<Folder>>() {
                    @Override
                    public Observable<Folder> call(File file) {
                        Folder folder = new Folder();
                        folder.setName(file.getName());
                        folder.setPath(file.getAbsolutePath());
                        return Observable.just(folder);
                    }
                })
                .toList()
                .flatMap(new Func1<List<Folder>, Observable<List<Folder>>>() {
                    @Override
                    public Observable<List<Folder>> call(List<Folder> folders) {
                        return mRepository.create(folders);
                    }
                })
                .doOnNext(new Action1<List<Folder>>() {
                    @Override
                    public void call(List<Folder> folders) {
                        Collections.sort(folders, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder f1, Folder f2) {
                                return f1.getName().compareToIgnoreCase(f2.getName());
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Folder>>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(List<Folder> allNewFolders) {
                        mView.onFoldersAdded(allNewFolders);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deleteFolder(Folder folder) {
        Subscription subscription = mRepository.delete(folder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Folder>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(Folder folder) {
                        mView.onFolderDeleted(folder);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void createPlayList(PlayList playList) {
        Subscription subscription = mRepository
                .create(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlayList>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(PlayList playList) {
                        mView.onPlayListCreated(playList);
                    }
                });
        mSubscriptions.add(subscription);
    }
}
