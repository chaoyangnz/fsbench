# fsbench


I modify some files to comiple for my purpose

# About shared memory

2T=1024*1024*1024 x 2k -> need too much share memory 

## reduce files to 0.5G

2T=1024*1024*512 x 4k -> need 66G share memory 

`FILEBENCH_NFILESETENTRIES	(1024 * 1024 * 512)`

```
filebench-shm: 67608 MB  -> 66G memory !!
shm_bitmap: 18874368 KB
shm_lastbitmapindex: 0 KB
shm_fileset: 1036 KB
shm_filesetentry: 50331648 KB
shm_procflow: 200 KB
shm_threadflow: 2952 KB
shm_flowop: 20480 KB
shm_var: 32 KB
shm_avd_ptrs: 64 KB
shm_strings: 128 KB
shm_filesetpaths: 0 KB
shm_cvar_heap: 64 KB
```

## reduce files to 64M

2T=1024*1024*64 x 32k

`FILEBENCH_NFILESETENTRIES	(1024 * 1024 * 64)`

```
set $nfiles=67108864
set $meandirwidth=10000
set $meanfilesize=32k

iosize=1m
memsize=10m

filebench-shm: 9496 MB  -> 9G memory nice!!
shm_bitmap: 2359296 KB
shm_lastbitmapindex: 0 KB
shm_fileset: 1036 KB
shm_filesetentry: 6291456 KB
shm_procflow: 200 KB
shm_threadflow: 2952 KB
shm_flowop: 20480 KB
shm_var: 32 KB
shm_avd_ptrs: 64 KB
shm_strings: 128 KB
shm_filesetpaths: 1048576 KB
shm_cvar_heap: 64 KB
```
