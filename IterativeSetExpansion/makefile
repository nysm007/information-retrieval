.PHONY: test upload clean bootstrap

test:
	sh -c '. _virtualenv/bin/activate; nosetests tests'

test-all:
	tox

upload: test-all
	python setup.py sdist bdist_wheel upload
	make clean

run:
	python3 index.py [token] [token] 4 0.35 "bill gates microsoft" 10

clean:
	rm -f MANIFEST
	rm -rf build dist

bootstrap: _virtualenv
	_virtualenv/bin/pip install -e .
ifneq ($(wildcard test-requirements.txt),)
	_virtualenv/bin/pip install -r test-requirements.txt
endif
	make clean

_virtualenv:
	virtualenv _virtualenv
	_virtualenv/bin/pip install --upgrade pip
	_virtualenv/bin/pip install --upgrade setuptools
