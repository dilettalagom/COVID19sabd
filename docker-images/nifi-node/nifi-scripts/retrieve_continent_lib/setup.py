import setuptools

setuptools.setup(
    name='retrieve_continent_lib',
    version='1.0',
    packages=['retrieve_continent_lib'],
    package_dir={'retrieve_continent_lib' : '.'}, # look for package contents in current directory
    package_data={'retrieve_continent_lib' : ['geocode.csv', 'countries.csv']},
    install_requires=['numpy', 'scipy', 'pycountry_convert']
)