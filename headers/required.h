#ifndef __REQUIRED_H__
#define __REQUIRED_H__

#ifdef ENABLE_FASTCGI
	#include "fcgi_stdio.h"
#else
	#include <stdio.h>
#endif
#include <stdlib.h>
#include <stdbool.h>
#include "qdecoder.h"

#include "config.h"
#include "errors.h"
#include "urls.h"
#include "accounts.h"
#include "lineitems.h"

/* Help Macro! CAll TOSTR on it!*/
#define TOSTR(x) STRINGIFY(x)
#define STRINGIFY(x) #x

#endif